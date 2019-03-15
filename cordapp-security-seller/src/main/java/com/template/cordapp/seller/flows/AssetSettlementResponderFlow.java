package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.exception.TooManyStatesFoundException;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.common.flows.IdentitySyncFlow.Receive;
import com.template.cordapp.contract.AssetContract;
import com.template.cordapp.flows.AbstractAssetSettlementFlow;
import com.template.cordapp.flows.FlowLogicCommonMethods;
import com.template.cordapp.flows.FlowLogicCommonMethods.DefaultImpls;
import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import com.template.cordapp.utils.UtilsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.AttachmentConstraint;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandAndState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.OwnableState;
import net.corda.core.contracts.PrivacySalt;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TimeWindow;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.ReceiveTransactionFlow;
import net.corda.core.flows.SendTransactionFlow;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.StatesToRecord;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import org.jetbrains.annotations.NotNull;

@InitiatedBy(AbstractAssetSettlementFlow.class)
@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u0000 \u000e2\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0003:\u0001\u000eB\r\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\r\u001a\u00020\u0002H\u0017R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\u00020\nX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u000f"},
   d2 = {"Lcom/synechron/cordapp/seller/flows/AssetSettlementResponderFlow;", "Lnet/corda/core/flows/FlowLogic;", "Lnet/corda/core/transactions/SignedTransaction;", "Lcom/synechron/cordapp/flows/FlowLogicCommonMethods;", "otherSideSession", "Lnet/corda/core/flows/FlowSession;", "(Lnet/corda/core/flows/FlowSession;)V", "getOtherSideSession", "()Lnet/corda/core/flows/FlowSession;", "progressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "getProgressTracker", "()Lnet/corda/core/utilities/ProgressTracker;", "call", "Companion", "cordapp-security-seller"}
)
public final class AssetSettlementResponderFlow extends FlowLogic implements FlowLogicCommonMethods {
   @NotNull
   private final ProgressTracker progressTracker;
   @NotNull
   private final FlowSession otherSideSession;
   public static final AssetSettlementResponderFlow.Companion Companion = new AssetSettlementResponderFlow.Companion((DefaultConstructorMarker)null);

   @NotNull
   public ProgressTracker getProgressTracker() {
      return this.progressTracker;
   }

   @Suspendable
   @NotNull
   public SignedTransaction call() {
      this.getProgressTracker().setCurrentStep((Step)AssetSettlementResponderFlow.Companion.ADD_ASSET.INSTANCE);
      SignedTransaction ptx1 = (SignedTransaction)this.subFlow((FlowLogic)(new ReceiveTransactionFlow(this.otherSideSession, false, StatesToRecord.NONE)));
      LedgerTransaction ltx1 = ptx1.toLedgerTransaction(this.getServiceHub(), false);
      Iterable $receiver$iv = (Iterable)ltx1.getInputStates();
      Collection destination$iv$iv = (Collection)(new ArrayList());
      Iterator var7 = $receiver$iv.iterator();

      while(var7.hasNext()) {
         Object element$iv$iv = var7.next();
         if (element$iv$iv instanceof AssetTransfer) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      AssetTransfer var10000 = (AssetTransfer)CollectionsKt.singleOrNull((List)destination$iv$iv);
      if (var10000 != null) {
         AssetTransfer assetTransfer = var10000;
         StateAndRef assetStateAndRef = UtilsKt.getAssetByCusip(this.getServiceHub(), assetTransfer.getAsset().getCusip());
         CommandAndState var13 = ((Asset)assetStateAndRef.getState().getData()).withNewOwner(assetTransfer.getSecurityBuyer());
         CommandData cmd = var13.component1();
         OwnableState assetOutState = var13.component2();
         TransactionBuilder txb = new TransactionBuilder(ltx1.getNotary(), (UUID)null, (List)null, (List)null, (List)null, (List)null, (TimeWindow)null, (PrivacySalt)null, 254, (DefaultConstructorMarker)null);
         txb.addInputState(assetStateAndRef);
         TransactionBuilder.addOutputState$default(txb, (ContractState)assetOutState, AssetContract.Companion.getASSET_CONTRACT_ID(), (AttachmentConstraint)null, 4, (Object)null);
         txb.addCommand(new Command(cmd, assetOutState.getOwner().getOwningKey()));
         SignedTransaction ptx2 = this.getServiceHub().signInitialTransaction(txb);
         this.subFlow((FlowLogic)(new SendTransactionFlow(this.otherSideSession, ptx2)));
         this.getProgressTracker().setCurrentStep((Step)AssetSettlementResponderFlow.Companion.SYNC_IDENTITY.INSTANCE);
         this.subFlow((FlowLogic)(new Receive(this.otherSideSession)));
         SignedTransaction stx = (SignedTransaction)this.subFlow((FlowLogic)(new SignTxFlow(this.otherSideSession)));
         return FlowLogic.waitForLedgerCommit$default(this, stx.getId(), false, 2, (Object)null);
      } else {
         throw (Throwable)(new TooManyStatesFoundException("Transaction with more than one `AssetTransfer` " + "input states received from `" + this.otherSideSession.getCounterparty() + "` party"));
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object call() {
      return this.call();
   }

   @NotNull
   public final FlowSession getOtherSideSession() {
      return this.otherSideSession;
   }

   public AssetSettlementResponderFlow(@NotNull FlowSession otherSideSession) {
      Intrinsics.checkParameterIsNotNull(otherSideSession, "otherSideSession");
      super();
      this.otherSideSession = otherSideSession;
      this.progressTracker = new ProgressTracker(new Step[]{(Step)AssetSettlementResponderFlow.Companion.ADD_ASSET.INSTANCE, (Step)AssetSettlementResponderFlow.Companion.SYNC_IDENTITY.INSTANCE});
   }

   @NotNull
   public Party firstNotary(@NotNull ServiceHub $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
      return DefaultImpls.firstNotary(this, $receiver);
   }

   @NotNull
   public StateAndRef loadState(@NotNull ServiceHub $receiver, @NotNull UniqueIdentifier linearId, @NotNull Class clazz) {
      Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
      Intrinsics.checkParameterIsNotNull(linearId, "linearId");
      Intrinsics.checkParameterIsNotNull(clazz, "clazz");
      return DefaultImpls.loadState(this, $receiver, linearId, clazz);
   }

   @NotNull
   public Party resolveIdentity(@NotNull ServiceHub $receiver, @NotNull AbstractParty abstractParty) {
      Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
      Intrinsics.checkParameterIsNotNull(abstractParty, "abstractParty");
      return DefaultImpls.resolveIdentity(this, $receiver, abstractParty);
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0002\u0003\u0004B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0005"},
      d2 = {"Lcom/synechron/cordapp/seller/flows/AssetSettlementResponderFlow$Companion;", "", "()V", "ADD_ASSET", "SYNC_IDENTITY", "cordapp-security-seller"}
   )
   public static final class Companion {
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
         d2 = {"Lcom/synechron/cordapp/seller/flows/AssetSettlementResponderFlow$Companion$ADD_ASSET;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-seller"}
      )
      public static final class ADD_ASSET extends Step {
         public static final AssetSettlementResponderFlow.Companion.ADD_ASSET INSTANCE;

         private ADD_ASSET() {
            super("Add Asset states to transaction builder.");
            INSTANCE = (AssetSettlementResponderFlow.Companion.ADD_ASSET)this;
         }

         static {
            new AssetSettlementResponderFlow.Companion.ADD_ASSET();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/seller/flows/AssetSettlementResponderFlow$Companion$SYNC_IDENTITY;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-seller"}
      )
      public static final class SYNC_IDENTITY extends Step {
         public static final AssetSettlementResponderFlow.Companion.SYNC_IDENTITY INSTANCE;

         private SYNC_IDENTITY() {
            super("Sync identities.");
            INSTANCE = (AssetSettlementResponderFlow.Companion.SYNC_IDENTITY)this;
         }

         static {
            new AssetSettlementResponderFlow.Companion.SYNC_IDENTITY();
         }
      }
   }
}

