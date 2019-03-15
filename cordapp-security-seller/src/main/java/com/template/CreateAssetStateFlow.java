package com.synechron.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.synechron.cordapp.contract.AssetContract;
import com.synechron.cordapp.contract.AssetContract.Commands.Create;
import com.synechron.cordapp.flows.FlowLogicCommonMethods;
import com.synechron.cordapp.flows.FlowLogicCommonMethods.DefaultImpls;
import com.synechron.cordapp.state.Asset;
import java.security.PublicKey;
import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.AttachmentConstraint;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.KotlinUtilsKt;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001\u0003B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0004"},
   d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetStateFlow;", "", "()V", "Initiator", "cordapp-security-seller"}
)
public final class CreateAssetStateFlow {
   public static final CreateAssetStateFlow INSTANCE;

   private CreateAssetStateFlow() {
      INSTANCE = (CreateAssetStateFlow)this;
   }

   static {
      new CreateAssetStateFlow();
   }

   @InitiatingFlow
   @StartableByRPC
   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u0000 \u00152\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0003:\u0001\u0015B#\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\u0002\u0010\nJ\b\u0010\u0014\u001a\u00020\u0002H\u0017R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0014\u0010\u000e\u001a\u00020\u000fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u0016"},
      d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetStateFlow$Initiator;", "Lnet/corda/core/flows/FlowLogic;", "Lnet/corda/core/transactions/SignedTransaction;", "Lcom/synechron/cordapp/flows/FlowLogicCommonMethods;", "cusip", "", "assetName", "purchaseCost", "Lnet/corda/core/contracts/Amount;", "Ljava/util/Currency;", "(Ljava/lang/String;Ljava/lang/String;Lnet/corda/core/contracts/Amount;)V", "getAssetName", "()Ljava/lang/String;", "getCusip", "progressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "getProgressTracker", "()Lnet/corda/core/utilities/ProgressTracker;", "getPurchaseCost", "()Lnet/corda/core/contracts/Amount;", "call", "Companion", "cordapp-security-seller"}
   )
   public static final class Initiator extends FlowLogic implements FlowLogicCommonMethods {
      @NotNull
      private final ProgressTracker progressTracker;
      @NotNull
      private final String cusip;
      @NotNull
      private final String assetName;
      @NotNull
      private final Amount purchaseCost;
      public static final CreateAssetStateFlow.Initiator.Companion Companion = new CreateAssetStateFlow.Initiator.Companion((DefaultConstructorMarker)null);

      @NotNull
      public ProgressTracker getProgressTracker() {
         return this.progressTracker;
      }

      @Suspendable
      @NotNull
      public SignedTransaction call() {
         this.getProgressTracker().setCurrentStep((Step)CreateAssetStateFlow.Initiator.Companion.INITIALISING.INSTANCE);
         Asset asset = new Asset(this.cusip, this.assetName, this.purchaseCost, (AbstractParty)this.getOurIdentity());
         this.getProgressTracker().setCurrentStep((Step)CreateAssetStateFlow.Initiator.Companion.BUILDING.INSTANCE);
         TransactionBuilder var10000 = new TransactionBuilder(this.firstNotary(this.getServiceHub()));
         ContractState var10001 = (ContractState)asset;
         com.synechron.cordapp.contract.AssetContract.Companion var10003 = AssetContract.Companion;
         var10000 = TransactionBuilder.addOutputState$default(var10000, var10001, AssetContract.Companion.getASSET_CONTRACT_ID(), (AttachmentConstraint)null, 4, (Object)null).addCommand((CommandData)(new Create()), new PublicKey[]{this.getOurIdentity().getOwningKey()});
         Instant var5 = this.getServiceHub().getClock().instant();
         Intrinsics.checkExpressionValueIsNotNull(var5, "serviceHub.clock.instant()");
         TransactionBuilder txb = var10000.setTimeWindow(var5, KotlinUtilsKt.getSeconds(30));
         this.getProgressTracker().setCurrentStep((Step)CreateAssetStateFlow.Initiator.Companion.SIGNING.INSTANCE);
         SignedTransaction stx = this.getServiceHub().signInitialTransaction(txb);
         this.getProgressTracker().setCurrentStep((Step)CreateAssetStateFlow.Initiator.Companion.FINALISING.INSTANCE);
         SignedTransaction ftx = (SignedTransaction)this.subFlow((FlowLogic)(new FinalityFlow(stx, CreateAssetStateFlow.Initiator.Companion.FINALISING.INSTANCE.childProgressTracker())));
         return ftx;
      }

      // $FF: synthetic method
      // $FF: bridge method
      public Object call() {
         return this.call();
      }

      @NotNull
      public final String getCusip() {
         return this.cusip;
      }

      @NotNull
      public final String getAssetName() {
         return this.assetName;
      }

      @NotNull
      public final Amount getPurchaseCost() {
         return this.purchaseCost;
      }

      public Initiator(@NotNull String cusip, @NotNull String assetName, @NotNull Amount purchaseCost) {
         Intrinsics.checkParameterIsNotNull(cusip, "cusip");
         Intrinsics.checkParameterIsNotNull(assetName, "assetName");
         Intrinsics.checkParameterIsNotNull(purchaseCost, "purchaseCost");
         super();
         this.cusip = cusip;
         this.assetName = assetName;
         this.purchaseCost = purchaseCost;
         this.progressTracker = Companion.tracker();
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
         d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0004\u0005\u0006\u0007\bB\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004¨\u0006\t"},
         d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetStateFlow$Initiator$Companion;", "", "()V", "tracker", "Lnet/corda/core/utilities/ProgressTracker;", "BUILDING", "FINALISING", "INITIALISING", "SIGNING", "cordapp-security-seller"}
      )
      public static final class Companion {
         @NotNull
         public final ProgressTracker tracker() {
            return new ProgressTracker(new Step[]{(Step)CreateAssetStateFlow.Initiator.Companion.INITIALISING.INSTANCE, (Step)CreateAssetStateFlow.Initiator.Companion.BUILDING.INSTANCE, (Step)CreateAssetStateFlow.Initiator.Companion.SIGNING.INSTANCE, (Step)CreateAssetStateFlow.Initiator.Companion.FINALISING.INSTANCE});
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
            d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetStateFlow$Initiator$Companion$INITIALISING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-seller"}
         )
         public static final class INITIALISING extends Step {
            public static final CreateAssetStateFlow.Initiator.Companion.INITIALISING INSTANCE;

            private INITIALISING() {
               super("Performing initial steps.");
               INSTANCE = (CreateAssetStateFlow.Initiator.Companion.INITIALISING)this;
            }

            static {
               new CreateAssetStateFlow.Initiator.Companion.INITIALISING();
            }
         }

         @Metadata(
            mv = {1, 1, 8},
            bv = {1, 0, 2},
            k = 1,
            d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
            d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetStateFlow$Initiator$Companion$BUILDING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-seller"}
         )
         public static final class BUILDING extends Step {
            public static final CreateAssetStateFlow.Initiator.Companion.BUILDING INSTANCE;

            private BUILDING() {
               super("Building and verifying transaction.");
               INSTANCE = (CreateAssetStateFlow.Initiator.Companion.BUILDING)this;
            }

            static {
               new CreateAssetStateFlow.Initiator.Companion.BUILDING();
            }
         }

         @Metadata(
            mv = {1, 1, 8},
            bv = {1, 0, 2},
            k = 1,
            d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
            d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetStateFlow$Initiator$Companion$SIGNING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-seller"}
         )
         public static final class SIGNING extends Step {
            public static final CreateAssetStateFlow.Initiator.Companion.SIGNING INSTANCE;

            private SIGNING() {
               super("Signing transaction.");
               INSTANCE = (CreateAssetStateFlow.Initiator.Companion.SIGNING)this;
            }

            static {
               new CreateAssetStateFlow.Initiator.Companion.SIGNING();
            }
         }

         @Metadata(
            mv = {1, 1, 8},
            bv = {1, 0, 2},
            k = 1,
            d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"},
            d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetStateFlow$Initiator$Companion$FINALISING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "childProgressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "cordapp-security-seller"}
         )
         public static final class FINALISING extends Step {
            public static final CreateAssetStateFlow.Initiator.Companion.FINALISING INSTANCE;

            @NotNull
            public ProgressTracker childProgressTracker() {
               return FinalityFlow.Companion.tracker();
            }

            private FINALISING() {
               super("Finalising transaction.");
               INSTANCE = (CreateAssetStateFlow.Initiator.Companion.FINALISING)this;
            }

            static {
               new CreateAssetStateFlow.Initiator.Companion.FINALISING();
            }
         }
      }
   }
}

