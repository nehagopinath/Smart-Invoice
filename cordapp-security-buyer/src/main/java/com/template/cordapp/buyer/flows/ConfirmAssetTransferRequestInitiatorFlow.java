package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.exception.InvalidPartyException;
import com.template.cordapp.common.flows.IdentitySyncFlow.Send;
import com.template.cordapp.contract.AssetTransferContract;
import com.template.cordapp.contract.AssetTransferContract.Commands.ConfirmRequest;
import com.template.cordapp.flows.AbstractConfirmAssetTransferRequestFlow;
import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import com.template.cordapp.state.RequestStatus;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.confidential.SwapIdentitiesFlow;
import net.corda.core.contracts.AttachmentConstraint;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.CollectSignaturesFlow;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.node.ServicesForResolution;
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
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u0000 \u00112\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0011B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\b\u0010\u0010\u001a\u00020\u0002H\u0017R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\rX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0012"},
   d2 = {"Lcom/synechron/cordapp/buyer/flows/ConfirmAssetTransferRequestInitiatorFlow;", "Lcom/synechron/cordapp/flows/AbstractConfirmAssetTransferRequestFlow;", "Lnet/corda/core/transactions/SignedTransaction;", "linearId", "Lnet/corda/core/contracts/UniqueIdentifier;", "clearingHouse", "Lnet/corda/core/identity/Party;", "(Lnet/corda/core/contracts/UniqueIdentifier;Lnet/corda/core/identity/Party;)V", "getClearingHouse", "()Lnet/corda/core/identity/Party;", "getLinearId", "()Lnet/corda/core/contracts/UniqueIdentifier;", "progressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "getProgressTracker", "()Lnet/corda/core/utilities/ProgressTracker;", "call", "Companion", "cordapp-security-buyer"}
)
public final class ConfirmAssetTransferRequestInitiatorFlow extends AbstractConfirmAssetTransferRequestFlow {
   @NotNull
   private final ProgressTracker progressTracker;
   @NotNull
   private final UniqueIdentifier linearId;
   @NotNull
   private final Party clearingHouse;
   public static final ConfirmAssetTransferRequestInitiatorFlow.Companion Companion = new ConfirmAssetTransferRequestInitiatorFlow.Companion((DefaultConstructorMarker)null);

   @NotNull
   public ProgressTracker getProgressTracker() {
      return this.progressTracker;
   }

   @Suspendable
   @NotNull
   public SignedTransaction call() {
      this.getProgressTracker().setCurrentStep((Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.SWAP_IDENTITY.INSTANCE);
      LinkedHashMap txKeys = (LinkedHashMap)this.subFlow((FlowLogic)(new SwapIdentitiesFlow(this.clearingHouse)));
      boolean var2 = txKeys.size() == 2;
      if (!var2) {
         String var23 = "Something went wrong when generating confidential identities.";
         throw (Throwable)(new IllegalStateException(var23.toString()));
      } else {
         AnonymousParty var10000 = (AnonymousParty)txKeys.get(this.clearingHouse);
         if (var10000 == null) {
            throw (Throwable)(new FlowException("Couldn't create anonymous identity for `" + this.clearingHouse + "` party."));
         } else {
            AnonymousParty anonymousCustodian = var10000;
            this.getProgressTracker().setCurrentStep((Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.INITIALISING.INSTANCE);
            StateAndRef input = this.loadState(this.getServiceHub(), this.linearId, AssetTransfer.class);
            Collection var32 = (Collection)((AssetTransfer)input.getState().getData()).getParticipants();
            Intrinsics.checkExpressionValueIsNotNull(anonymousCustodian, "anonymousCustodian");
            List participants = CollectionsKt.plus(var32, anonymousCustodian);
            AssetTransfer var33 = (AssetTransfer)input.getState().getData();
            AbstractParty var10004 = (AbstractParty)anonymousCustodian;
            TransactionBuilder txb = null;
            RequestStatus var7 = RequestStatus.PENDING;
            AssetTransfer output = AssetTransfer.copy$default(var33, (Asset)null, (AbstractParty)null, (AbstractParty)null, var10004, var7, participants, txb, 71, (Object)null);
            if (Intrinsics.areEqual(this.getOurIdentity().getName(), this.resolveIdentity(this.getServiceHub(), output.getSecurityBuyer()).getName()) ^ true) {
               throw (Throwable)(new InvalidPartyException("Flow must be initiated by Lender Of Cash."));
            } else {
               this.getProgressTracker().setCurrentStep((Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.BUILDING.INSTANCE);
               TransactionBuilder var34 = (new TransactionBuilder(input.getState().getNotary())).addInputState(input);
               ContractState var10001 = (ContractState)output;
               com.synechron.cordapp.contract.AssetTransferContract.Companion var10003 = AssetTransferContract.Companion;
               var34 = TransactionBuilder.addOutputState$default(var34, var10001, AssetTransferContract.Companion.getASSET_TRANSFER_CONTRACT_ID(), (AttachmentConstraint)null, 4, (Object)null);
               CommandData var35 = (CommandData)(new ConfirmRequest());
               Iterable $receiver$iv = (Iterable)participants;
               CommandData var19 = var35;
               TransactionBuilder var18 = var34;
               Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
               Iterator var10 = $receiver$iv.iterator();

               while(var10.hasNext()) {
                  Object item$iv$iv = var10.next();
                  AbstractParty it = (AbstractParty)item$iv$iv;
                  PublicKey var21 = it.getOwningKey();
                  destination$iv$iv.add(var21);
               }

               List var20 = (List)destination$iv$iv;
               var34 = var18.addCommand(var19, var20);
               Instant var36 = this.getServiceHub().getClock().instant();
               Intrinsics.checkExpressionValueIsNotNull(var36, "serviceHub.clock.instant()");
               txb = var34.setTimeWindow(var36, KotlinUtilsKt.getSeconds(60));
               this.getProgressTracker().setCurrentStep((Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.SIGNING.INSTANCE);
               SignedTransaction ptx = this.getServiceHub().signInitialTransaction(txb, output.getSecurityBuyer().getOwningKey());
               Iterable $receiver$iv = (Iterable)participants;
               Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
               Iterator var30 = $receiver$iv.iterator();

               Object item$iv$iv;
               while(var30.hasNext()) {
                  item$iv$iv = var30.next();
                  AbstractParty it = (AbstractParty)item$iv$iv;
                  Party var37 = this.resolveIdentity(this.getServiceHub(), it);
                  destination$iv$iv.add(var37);
               }

               $receiver$iv = (Iterable)((List)destination$iv$iv);
               destination$iv$iv = (Collection)(new ArrayList());
               var30 = $receiver$iv.iterator();

               Party it;
               while(var30.hasNext()) {
                  item$iv$iv = var30.next();
                  it = (Party)item$iv$iv;
                  if (Intrinsics.areEqual(it.getName(), this.getOurIdentity().getName()) ^ true) {
                     destination$iv$iv.add(item$iv$iv);
                  }
               }

               $receiver$iv = (Iterable)((List)destination$iv$iv);
               destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
               var30 = $receiver$iv.iterator();

               while(var30.hasNext()) {
                  item$iv$iv = var30.next();
                  it = (Party)item$iv$iv;
                  FlowSession var38 = this.initiateFlow(it);
                  destination$iv$iv.add(var38);
               }

               Set counterPartySessions = CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
               this.getProgressTracker().setCurrentStep((Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.IDENTITY_SYNC.INSTANCE);
               this.subFlow((FlowLogic)(new Send(counterPartySessions, txb.toWireTransaction((ServicesForResolution)this.getServiceHub()), ConfirmAssetTransferRequestInitiatorFlow.Companion.IDENTITY_SYNC.INSTANCE.childProgressTracker())));
               this.getProgressTracker().setCurrentStep((Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.COLLECTING.INSTANCE);
               SignedTransaction stx = (SignedTransaction)this.subFlow((FlowLogic)(new CollectSignaturesFlow(ptx, (Collection)counterPartySessions, (Iterable)CollectionsKt.listOf(output.getSecurityBuyer().getOwningKey()), ConfirmAssetTransferRequestInitiatorFlow.Companion.COLLECTING.INSTANCE.childProgressTracker())));
               this.getProgressTracker().setCurrentStep((Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.FINALISING.INSTANCE);
               SignedTransaction ftx = (SignedTransaction)this.subFlow((FlowLogic)(new FinalityFlow(stx, ConfirmAssetTransferRequestInitiatorFlow.Companion.FINALISING.INSTANCE.childProgressTracker())));
               return ftx;
            }
         }
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

   @NotNull
   public final Party getClearingHouse() {
      return this.clearingHouse;
   }

   public ConfirmAssetTransferRequestInitiatorFlow(@NotNull UniqueIdentifier linearId, @NotNull Party clearingHouse) {
      Intrinsics.checkParameterIsNotNull(linearId, "linearId");
      Intrinsics.checkParameterIsNotNull(clearingHouse, "clearingHouse");
      super();
      this.linearId = linearId;
      this.clearingHouse = clearingHouse;
      this.progressTracker = Companion.tracker();
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0007\u0005\u0006\u0007\b\t\n\u000bB\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004¨\u0006\f"},
      d2 = {"Lcom/synechron/cordapp/buyer/flows/ConfirmAssetTransferRequestInitiatorFlow$Companion;", "", "()V", "tracker", "Lnet/corda/core/utilities/ProgressTracker;", "BUILDING", "COLLECTING", "FINALISING", "IDENTITY_SYNC", "INITIALISING", "SIGNING", "SWAP_IDENTITY", "cordapp-security-buyer"}
   )
   public static final class Companion {
      @NotNull
      public final ProgressTracker tracker() {
         return new ProgressTracker(new Step[]{(Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.SWAP_IDENTITY.INSTANCE, (Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.INITIALISING.INSTANCE, (Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.BUILDING.INSTANCE, (Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.SIGNING.INSTANCE, (Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.IDENTITY_SYNC.INSTANCE, (Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.COLLECTING.INSTANCE, (Step)ConfirmAssetTransferRequestInitiatorFlow.Companion.FINALISING.INSTANCE});
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
         d2 = {"Lcom/synechron/cordapp/buyer/flows/ConfirmAssetTransferRequestInitiatorFlow$Companion$SWAP_IDENTITY;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-buyer"}
      )
      public static final class SWAP_IDENTITY extends Step {
         public static final ConfirmAssetTransferRequestInitiatorFlow.Companion.SWAP_IDENTITY INSTANCE;

         private SWAP_IDENTITY() {
            super("Swap Identity.");
            INSTANCE = (ConfirmAssetTransferRequestInitiatorFlow.Companion.SWAP_IDENTITY)this;
         }

         static {
            new ConfirmAssetTransferRequestInitiatorFlow.Companion.SWAP_IDENTITY();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/buyer/flows/ConfirmAssetTransferRequestInitiatorFlow$Companion$INITIALISING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-buyer"}
      )
      public static final class INITIALISING extends Step {
         public static final ConfirmAssetTransferRequestInitiatorFlow.Companion.INITIALISING INSTANCE;

         private INITIALISING() {
            super("Performing initial steps.");
            INSTANCE = (ConfirmAssetTransferRequestInitiatorFlow.Companion.INITIALISING)this;
         }

         static {
            new ConfirmAssetTransferRequestInitiatorFlow.Companion.INITIALISING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/buyer/flows/ConfirmAssetTransferRequestInitiatorFlow$Companion$BUILDING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-buyer"}
      )
      public static final class BUILDING extends Step {
         public static final ConfirmAssetTransferRequestInitiatorFlow.Companion.BUILDING INSTANCE;

         private BUILDING() {
            super("Building and verifying transaction.");
            INSTANCE = (ConfirmAssetTransferRequestInitiatorFlow.Companion.BUILDING)this;
         }

         static {
            new ConfirmAssetTransferRequestInitiatorFlow.Companion.BUILDING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/buyer/flows/ConfirmAssetTransferRequestInitiatorFlow$Companion$SIGNING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-buyer"}
      )
      public static final class SIGNING extends Step {
         public static final ConfirmAssetTransferRequestInitiatorFlow.Companion.SIGNING INSTANCE;

         private SIGNING() {
            super("Signing transaction.");
            INSTANCE = (ConfirmAssetTransferRequestInitiatorFlow.Companion.SIGNING)this;
         }

         static {
            new ConfirmAssetTransferRequestInitiatorFlow.Companion.SIGNING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"},
         d2 = {"Lcom/synechron/cordapp/buyer/flows/ConfirmAssetTransferRequestInitiatorFlow$Companion$IDENTITY_SYNC;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "childProgressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "cordapp-security-buyer"}
      )
      public static final class IDENTITY_SYNC extends Step {
         public static final ConfirmAssetTransferRequestInitiatorFlow.Companion.IDENTITY_SYNC INSTANCE;

         @NotNull
         public ProgressTracker childProgressTracker() {
            return Send.Companion.tracker();
         }

         private IDENTITY_SYNC() {
            super("Sync identities with counter parties.");
            INSTANCE = (ConfirmAssetTransferRequestInitiatorFlow.Companion.IDENTITY_SYNC)this;
         }

         static {
            new ConfirmAssetTransferRequestInitiatorFlow.Companion.IDENTITY_SYNC();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"},
         d2 = {"Lcom/synechron/cordapp/buyer/flows/ConfirmAssetTransferRequestInitiatorFlow$Companion$COLLECTING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "childProgressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "cordapp-security-buyer"}
      )
      public static final class COLLECTING extends Step {
         public static final ConfirmAssetTransferRequestInitiatorFlow.Companion.COLLECTING INSTANCE;

         @NotNull
         public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.Companion.tracker();
         }

         private COLLECTING() {
            super("Collecting counterparty signature.");
            INSTANCE = (ConfirmAssetTransferRequestInitiatorFlow.Companion.COLLECTING)this;
         }

         static {
            new ConfirmAssetTransferRequestInitiatorFlow.Companion.COLLECTING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"},
         d2 = {"Lcom/synechron/cordapp/buyer/flows/ConfirmAssetTransferRequestInitiatorFlow$Companion$FINALISING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "childProgressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "cordapp-security-buyer"}
      )
      public static final class FINALISING extends Step {
         public static final ConfirmAssetTransferRequestInitiatorFlow.Companion.FINALISING INSTANCE;

         @NotNull
         public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
         }

         private FINALISING() {
            super("Finalising transaction.");
            INSTANCE = (ConfirmAssetTransferRequestInitiatorFlow.Companion.FINALISING)this;
         }

         static {
            new ConfirmAssetTransferRequestInitiatorFlow.Companion.FINALISING();
         }
      }
   }
}

