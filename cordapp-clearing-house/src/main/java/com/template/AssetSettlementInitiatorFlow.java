package com.synechron.cordapp.clearinghouse.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.synechron.cordapp.common.exception.InvalidPartyException;
import com.synechron.cordapp.common.flows.ReceiveTransactionUnVerifiedFlow;
import com.synechron.cordapp.common.flows.IdentitySyncFlow.Send;
import com.synechron.cordapp.contract.AssetTransferContract;
import com.synechron.cordapp.contract.AssetTransferContract.Commands.SettleRequest;
import com.synechron.cordapp.flows.AbstractAssetSettlementFlow;
import com.synechron.cordapp.state.Asset;
import com.synechron.cordapp.state.AssetTransfer;
import com.synechron.cordapp.state.RequestStatus;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.confidential.IdentitySyncFlow.Receive;
import net.corda.core.contracts.AttachmentConstraint;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.CollectSignaturesFlow;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.SendTransactionFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.ServicesForResolution;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.KotlinUtilsKt;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import org.jetbrains.annotations.NotNull;

@StartableByRPC
@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u0000 \r2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\rB\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\b\u0010\f\u001a\u00020\u0002H\u0017R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u000e"},
   d2 = {"Lcom/synechron/cordapp/clearinghouse/flows/AssetSettlementInitiatorFlow;", "Lcom/synechron/cordapp/flows/AbstractAssetSettlementFlow;", "Lnet/corda/core/transactions/SignedTransaction;", "linearId", "Lnet/corda/core/contracts/UniqueIdentifier;", "(Lnet/corda/core/contracts/UniqueIdentifier;)V", "getLinearId", "()Lnet/corda/core/contracts/UniqueIdentifier;", "progressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "getProgressTracker", "()Lnet/corda/core/utilities/ProgressTracker;", "call", "Companion", "cordapp-clearing-house"}
)
public final class AssetSettlementInitiatorFlow extends AbstractAssetSettlementFlow {
   @NotNull
   private final ProgressTracker progressTracker;
   @NotNull
   private final UniqueIdentifier linearId;
   public static final AssetSettlementInitiatorFlow.Companion Companion = new AssetSettlementInitiatorFlow.Companion((DefaultConstructorMarker)null);

   @NotNull
   public ProgressTracker getProgressTracker() {
      return this.progressTracker;
   }

   @Suspendable
   @NotNull
   public SignedTransaction call() {
      this.getProgressTracker().setCurrentStep((Step)AssetSettlementInitiatorFlow.Companion.INITIALISING.INSTANCE);
      StateAndRef inAssetTransfer = this.loadState(this.getServiceHub(), this.linearId, AssetTransfer.class);
      List participants = ((AssetTransfer)inAssetTransfer.getState().getData()).getParticipants();
      AssetTransfer outAssetTransfer = AssetTransfer.copy$default((AssetTransfer)inAssetTransfer.getState().getData(), (Asset)null, (AbstractParty)null, (AbstractParty)null, (AbstractParty)null, RequestStatus.TRANSFERRED, (List)null, (UniqueIdentifier)null, 111, (Object)null);
      CordaX500Name var10000 = this.getOurIdentity().getName();
      ServiceHub var10002 = this.getServiceHub();
      AbstractParty var10003 = outAssetTransfer.getClearingHouse();
      if (var10003 == null) {
         Intrinsics.throwNpe();
      }

      if (Intrinsics.areEqual(var10000, this.resolveIdentity(var10002, var10003).getName()) ^ true) {
         throw (Throwable)(new InvalidPartyException("Flow must be initiated by Custodian."));
      } else {
         this.getProgressTracker().setCurrentStep((Step)AssetSettlementInitiatorFlow.Companion.BUILDING.INSTANCE);
         TransactionBuilder var39 = TransactionBuilder.addOutputState$default((new TransactionBuilder(inAssetTransfer.getState().getNotary())).addInputState(inAssetTransfer), (ContractState)outAssetTransfer, AssetTransferContract.Companion.getASSET_TRANSFER_CONTRACT_ID(), (AttachmentConstraint)null, 4, (Object)null);
         CommandData var10001 = (CommandData)(new SettleRequest());
         Iterable $receiver$iv = (Iterable)participants;
         CommandData var19 = var10001;
         TransactionBuilder var18 = var39;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
         Iterator var8 = $receiver$iv.iterator();

         while(var8.hasNext()) {
            Object item$iv$iv = var8.next();
            AbstractParty it = (AbstractParty)item$iv$iv;
            PublicKey var21 = it.getOwningKey();
            destination$iv$iv.add(var21);
         }

         List var20 = (List)destination$iv$iv;
         TransactionBuilder txb = var18.addCommand(var19, var20);
         this.getProgressTracker().setCurrentStep((Step)AssetSettlementInitiatorFlow.Companion.COLLECT_STATES.INSTANCE);
         SignedTransaction tempPtx = this.getServiceHub().signInitialTransaction(txb);
         FlowSession securitySellerSession = this.initiateFlow(this.resolveIdentity(this.getServiceHub(), outAssetTransfer.getSecuritySeller()));
         this.subFlow((FlowLogic)(new SendTransactionFlow(securitySellerSession, tempPtx)));
         SignedTransaction assetPtx = (SignedTransaction)this.subFlow((FlowLogic)(new ReceiveTransactionUnVerifiedFlow(securitySellerSession)));
         FlowSession securityBuyerSession = this.initiateFlow(this.resolveIdentity(this.getServiceHub(), outAssetTransfer.getSecurityBuyer()));
         this.subFlow((FlowLogic)(new SendTransactionFlow(securityBuyerSession, tempPtx)));
         securityBuyerSession.send(txb.getLockId());
         this.subFlow((FlowLogic)(new Receive(securityBuyerSession)));
         SignedTransaction cashPtx = (SignedTransaction)this.subFlow((FlowLogic)(new ReceiveTransactionUnVerifiedFlow(securityBuyerSession)));
         LedgerTransaction assetLtx = assetPtx.toLedgerTransaction(this.getServiceHub(), false);
         Iterable $receiver$iv = (Iterable)assetLtx.getInputs();
         Iterator var12 = $receiver$iv.iterator();

         Object element$iv;
         while(var12.hasNext()) {
            element$iv = var12.next();
            StateAndRef it = (StateAndRef)element$iv;
            txb.addInputState(it);
         }

         $receiver$iv = (Iterable)assetLtx.getOutputs();
         var12 = $receiver$iv.iterator();

         while(var12.hasNext()) {
            element$iv = var12.next();
            TransactionState it = (TransactionState)element$iv;
            txb.addOutputState(it);
         }

         $receiver$iv = (Iterable)assetLtx.getCommands();
         var12 = $receiver$iv.iterator();

         while(var12.hasNext()) {
            element$iv = var12.next();
            CommandWithParties it = (CommandWithParties)element$iv;
            txb.addCommand(new Command(it.getValue(), it.getSigners()));
         }

         LedgerTransaction cashLtx = cashPtx.toLedgerTransaction(this.getServiceHub(), false);
         Iterable $receiver$iv = (Iterable)cashLtx.getInputs();
         Iterator var29 = $receiver$iv.iterator();

         Object element$iv;
         while(var29.hasNext()) {
            element$iv = var29.next();
            StateAndRef it = (StateAndRef)element$iv;
            txb.addInputState(it);
         }

         $receiver$iv = (Iterable)cashLtx.getOutputs();
         var29 = $receiver$iv.iterator();

         while(var29.hasNext()) {
            element$iv = var29.next();
            TransactionState it = (TransactionState)element$iv;
            txb.addOutputState(it);
         }

         $receiver$iv = (Iterable)cashLtx.getCommands();
         var29 = $receiver$iv.iterator();

         while(var29.hasNext()) {
            element$iv = var29.next();
            CommandWithParties it = (CommandWithParties)element$iv;
            txb.addCommand(new Command(it.getValue(), it.getSigners()));
         }

         this.getProgressTracker().setCurrentStep((Step)AssetSettlementInitiatorFlow.Companion.IDENTITY_SYNC.INSTANCE);
         Set counterPartySessions = SetsKt.setOf(new FlowSession[]{securityBuyerSession, securitySellerSession});
         this.subFlow((FlowLogic)(new Send(counterPartySessions, txb.toWireTransaction((ServicesForResolution)this.getServiceHub()), AssetSettlementInitiatorFlow.Companion.IDENTITY_SYNC.INSTANCE.childProgressTracker())));
         this.getProgressTracker().setCurrentStep((Step)AssetSettlementInitiatorFlow.Companion.SIGNING.INSTANCE);
         Instant var41 = this.getServiceHub().getClock().instant();
         Intrinsics.checkExpressionValueIsNotNull(var41, "serviceHub.clock.instant()");
         txb.setTimeWindow(var41, KotlinUtilsKt.getSeconds(60));
         ServiceHub var40 = this.getServiceHub();
         AbstractParty var43 = outAssetTransfer.getClearingHouse();
         if (var43 == null) {
            Intrinsics.throwNpe();
         }

         SignedTransaction ptx = var40.signInitialTransaction(txb, var43.getOwningKey());
         this.getProgressTracker().setCurrentStep((Step)AssetSettlementInitiatorFlow.Companion.COLLECTING.INSTANCE);
         CollectSignaturesFlow var42 = new CollectSignaturesFlow;
         Collection var10004 = (Collection)counterPartySessions;
         AbstractParty var10005 = outAssetTransfer.getClearingHouse();
         if (var10005 == null) {
            Intrinsics.throwNpe();
         }

         var42.<init>(ptx, var10004, (Iterable)CollectionsKt.listOf(var10005.getOwningKey()), AssetSettlementInitiatorFlow.Companion.COLLECTING.INSTANCE.childProgressTracker());
         SignedTransaction stx = (SignedTransaction)this.subFlow((FlowLogic)var42);
         this.getProgressTracker().setCurrentStep((Step)AssetSettlementInitiatorFlow.Companion.FINALISING.INSTANCE);
         SignedTransaction ftx = (SignedTransaction)this.subFlow((FlowLogic)(new FinalityFlow(stx, AssetSettlementInitiatorFlow.Companion.FINALISING.INSTANCE.childProgressTracker())));
         return ftx;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object call() {
      return this.call();
   }

   @NotNull
   public final UniqueIdentifier getLinearId() {
      return this.linearId;
   }

   public AssetSettlementInitiatorFlow(@NotNull UniqueIdentifier linearId) {
      Intrinsics.checkParameterIsNotNull(linearId, "linearId");
      super();
      this.linearId = linearId;
      this.progressTracker = Companion.tracker();
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0007\u0005\u0006\u0007\b\t\n\u000bB\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004¨\u0006\f"},
      d2 = {"Lcom/synechron/cordapp/clearinghouse/flows/AssetSettlementInitiatorFlow$Companion;", "", "()V", "tracker", "Lnet/corda/core/utilities/ProgressTracker;", "BUILDING", "COLLECTING", "COLLECT_STATES", "FINALISING", "IDENTITY_SYNC", "INITIALISING", "SIGNING", "cordapp-clearing-house"}
   )
   public static final class Companion {
      @NotNull
      public final ProgressTracker tracker() {
         return new ProgressTracker(new Step[]{(Step)AssetSettlementInitiatorFlow.Companion.INITIALISING.INSTANCE, (Step)AssetSettlementInitiatorFlow.Companion.BUILDING.INSTANCE, (Step)AssetSettlementInitiatorFlow.Companion.COLLECT_STATES.INSTANCE, (Step)AssetSettlementInitiatorFlow.Companion.IDENTITY_SYNC.INSTANCE, (Step)AssetSettlementInitiatorFlow.Companion.SIGNING.INSTANCE, (Step)AssetSettlementInitiatorFlow.Companion.COLLECTING.INSTANCE, (Step)AssetSettlementInitiatorFlow.Companion.FINALISING.INSTANCE});
      }

      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/clearinghouse/flows/AssetSettlementInitiatorFlow$Companion$INITIALISING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-clearing-house"}
      )
      public static final class INITIALISING extends Step {
         public static final AssetSettlementInitiatorFlow.Companion.INITIALISING INSTANCE;

         private INITIALISING() {
            super("Performing initial steps.");
            INSTANCE = (AssetSettlementInitiatorFlow.Companion.INITIALISING)this;
         }

         static {
            new AssetSettlementInitiatorFlow.Companion.INITIALISING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/clearinghouse/flows/AssetSettlementInitiatorFlow$Companion$BUILDING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-clearing-house"}
      )
      public static final class BUILDING extends Step {
         public static final AssetSettlementInitiatorFlow.Companion.BUILDING INSTANCE;

         private BUILDING() {
            super("Building and verifying transaction.");
            INSTANCE = (AssetSettlementInitiatorFlow.Companion.BUILDING)this;
         }

         static {
            new AssetSettlementInitiatorFlow.Companion.BUILDING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/clearinghouse/flows/AssetSettlementInitiatorFlow$Companion$COLLECT_STATES;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-clearing-house"}
      )
      public static final class COLLECT_STATES extends Step {
         public static final AssetSettlementInitiatorFlow.Companion.COLLECT_STATES INSTANCE;

         private COLLECT_STATES() {
            super("Collect Asset and Cash states from counterparty.");
            INSTANCE = (AssetSettlementInitiatorFlow.Companion.COLLECT_STATES)this;
         }

         static {
            new AssetSettlementInitiatorFlow.Companion.COLLECT_STATES();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"},
         d2 = {"Lcom/synechron/cordapp/clearinghouse/flows/AssetSettlementInitiatorFlow$Companion$IDENTITY_SYNC;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "childProgressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "cordapp-clearing-house"}
      )
      public static final class IDENTITY_SYNC extends Step {
         public static final AssetSettlementInitiatorFlow.Companion.IDENTITY_SYNC INSTANCE;

         @NotNull
         public ProgressTracker childProgressTracker() {
            return Send.Companion.tracker();
         }

         private IDENTITY_SYNC() {
            super("Sync identities with counter parties.");
            INSTANCE = (AssetSettlementInitiatorFlow.Companion.IDENTITY_SYNC)this;
         }

         static {
            new AssetSettlementInitiatorFlow.Companion.IDENTITY_SYNC();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/clearinghouse/flows/AssetSettlementInitiatorFlow$Companion$SIGNING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-clearing-house"}
      )
      public static final class SIGNING extends Step {
         public static final AssetSettlementInitiatorFlow.Companion.SIGNING INSTANCE;

         private SIGNING() {
            super("Signing transaction.");
            INSTANCE = (AssetSettlementInitiatorFlow.Companion.SIGNING)this;
         }

         static {
            new AssetSettlementInitiatorFlow.Companion.SIGNING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"},
         d2 = {"Lcom/synechron/cordapp/clearinghouse/flows/AssetSettlementInitiatorFlow$Companion$COLLECTING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "childProgressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "cordapp-clearing-house"}
      )
      public static final class COLLECTING extends Step {
         public static final AssetSettlementInitiatorFlow.Companion.COLLECTING INSTANCE;

         @NotNull
         public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.Companion.tracker();
         }

         private COLLECTING() {
            super("Collecting counterparty signature.");
            INSTANCE = (AssetSettlementInitiatorFlow.Companion.COLLECTING)this;
         }

         static {
            new AssetSettlementInitiatorFlow.Companion.COLLECTING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"},
         d2 = {"Lcom/synechron/cordapp/clearinghouse/flows/AssetSettlementInitiatorFlow$Companion$FINALISING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "childProgressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "cordapp-clearing-house"}
      )
      public static final class FINALISING extends Step {
         public static final AssetSettlementInitiatorFlow.Companion.FINALISING INSTANCE;

         @NotNull
         public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
         }

         private FINALISING() {
            super("Finalising transaction.");
            INSTANCE = (AssetSettlementInitiatorFlow.Companion.FINALISING)this;
         }

         static {
            new AssetSettlementInitiatorFlow.Companion.FINALISING();
         }
      }
   }
}

