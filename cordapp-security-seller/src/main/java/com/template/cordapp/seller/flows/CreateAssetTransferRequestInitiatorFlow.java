package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.exception.InvalidPartyException;
import com.template.cordapp.contract.AssetTransferContract;
import com.template.cordapp.contract.AssetTransferContract.Commands.CreateRequest;
import com.template.cordapp.flows.AbstractCreateAssetTransferRequestFlow;
import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import com.template.cordapp.state.RequestStatus;
import com.template.cordapp.utils.UtilsKt;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.confidential.SwapIdentitiesFlow;
import net.corda.core.contracts.AttachmentConstraint;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
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
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u0000 \u00112\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0011B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\b\u0010\u0010\u001a\u00020\u0002H\u0017R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\n\u001a\u00020\u000bX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0012"},
   d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetTransferRequestInitiatorFlow;", "Lcom/synechron/cordapp/flows/AbstractCreateAssetTransferRequestFlow;", "Lnet/corda/core/transactions/SignedTransaction;", "cusip", "", "securityBuyer", "Lnet/corda/core/identity/Party;", "(Ljava/lang/String;Lnet/corda/core/identity/Party;)V", "getCusip", "()Ljava/lang/String;", "progressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "getProgressTracker", "()Lnet/corda/core/utilities/ProgressTracker;", "getSecurityBuyer", "()Lnet/corda/core/identity/Party;", "call", "Companion", "cordapp-security-seller"}
)
public final class CreateAssetTransferRequestInitiatorFlow extends AbstractCreateAssetTransferRequestFlow {
   @NotNull
   private final ProgressTracker progressTracker;
   @NotNull
   private final String cusip;
   @NotNull
   private final Party securityBuyer;
   public static final CreateAssetTransferRequestInitiatorFlow.Companion Companion = new CreateAssetTransferRequestInitiatorFlow.Companion((DefaultConstructorMarker)null);

   @NotNull
   public ProgressTracker getProgressTracker() {
      return this.progressTracker;
   }

   @Suspendable
   @NotNull
   public SignedTransaction call() {
      if (Intrinsics.areEqual(this.getOurIdentity().getName(), this.securityBuyer.getName())) {
         throw (Throwable)(new InvalidPartyException("Flow initiating party should not equals to Lender of Cash party."));
      } else {
         this.getProgressTracker().setCurrentStep((Step)CreateAssetTransferRequestInitiatorFlow.Companion.INITIALISING.INSTANCE);
         LinkedHashMap txKeys = (LinkedHashMap)this.subFlow((FlowLogic)(new SwapIdentitiesFlow(this.securityBuyer)));
         boolean var2 = txKeys.size() == 2;
         if (!var2) {
            String var22 = "Something went wrong when generating confidential identities.";
            throw (Throwable)(new IllegalStateException(var22.toString()));
         } else {
            AnonymousParty var10000 = (AnonymousParty)txKeys.get(this.getOurIdentity());
            if (var10000 == null) {
               throw (Throwable)(new FlowException("Couldn't create our anonymous identity."));
            } else {
               AnonymousParty anonymousMe = var10000;
               var10000 = (AnonymousParty)txKeys.get(this.securityBuyer);
               if (var10000 == null) {
                  throw (Throwable)(new FlowException("Couldn't create lender's (securityBuyer) anonymous identity."));
               } else {
                  AnonymousParty anonymousCashLender = var10000;
                  Asset asset = (Asset)UtilsKt.getAssetByCusip(this.getServiceHub(), this.cusip).getState().getData();
                  Intrinsics.checkExpressionValueIsNotNull(anonymousMe, "anonymousMe");
                  AbstractParty var10003 = (AbstractParty)anonymousMe;
                  Intrinsics.checkExpressionValueIsNotNull(anonymousCashLender, "anonymousCashLender");
                  AssetTransfer assetTransfer = new AssetTransfer(asset, var10003, (AbstractParty)anonymousCashLender, (AbstractParty)null, RequestStatus.PENDING_CONFIRMATION, (List)null, (UniqueIdentifier)null, 96, (DefaultConstructorMarker)null);
                  PublicKey ourSigningKey = assetTransfer.getSecuritySeller().getOwningKey();
                  this.getProgressTracker().setCurrentStep((Step)CreateAssetTransferRequestInitiatorFlow.Companion.BUILDING.INSTANCE);
                  TransactionBuilder var26 = new TransactionBuilder(this.firstNotary(this.getServiceHub()));
                  ContractState var10001 = (ContractState)assetTransfer;
                  com.synechron.cordapp.contract.AssetTransferContract.Companion var29 = AssetTransferContract.Companion;
                  var26 = TransactionBuilder.addOutputState$default(var26, var10001, AssetTransferContract.Companion.getASSET_TRANSFER_CONTRACT_ID(), (AttachmentConstraint)null, 4, (Object)null);
                  CommandData var27 = (CommandData)(new CreateRequest());
                  Iterable $receiver$iv = (Iterable)assetTransfer.getParticipants();
                  CommandData var18 = var27;
                  TransactionBuilder var17 = var26;
                  Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
                  Iterator var11 = $receiver$iv.iterator();

                  while(var11.hasNext()) {
                     Object item$iv$iv = var11.next();
                     AbstractParty it = (AbstractParty)item$iv$iv;
                     PublicKey var20 = it.getOwningKey();
                     destination$iv$iv.add(var20);
                  }

                  List var19 = (List)destination$iv$iv;
                  var26 = var17.addCommand(var18, var19);
                  Instant var28 = this.getServiceHub().getClock().instant();
                  Intrinsics.checkExpressionValueIsNotNull(var28, "serviceHub.clock.instant()");
                  TransactionBuilder txb = var26.setTimeWindow(var28, KotlinUtilsKt.getSeconds(30));
                  this.getProgressTracker().setCurrentStep((Step)CreateAssetTransferRequestInitiatorFlow.Companion.SIGNING.INSTANCE);
                  SignedTransaction ptx = this.getServiceHub().signInitialTransaction(txb, ourSigningKey);
                  this.getProgressTracker().setCurrentStep((Step)CreateAssetTransferRequestInitiatorFlow.Companion.COLLECTING.INSTANCE);
                  FlowSession lenderOfCashFlowSession = this.initiateFlow(this.securityBuyer);
                  SignedTransaction stx = (SignedTransaction)this.subFlow((FlowLogic)(new CollectSignaturesFlow(ptx, (Collection)SetsKt.setOf(lenderOfCashFlowSession), (Iterable)CollectionsKt.listOf(ourSigningKey), CreateAssetTransferRequestInitiatorFlow.Companion.COLLECTING.INSTANCE.childProgressTracker())));
                  this.getProgressTracker().setCurrentStep((Step)CreateAssetTransferRequestInitiatorFlow.Companion.FINALISING.INSTANCE);
                  SignedTransaction ftx = (SignedTransaction)this.subFlow((FlowLogic)(new FinalityFlow(stx, CreateAssetTransferRequestInitiatorFlow.Companion.FINALISING.INSTANCE.childProgressTracker())));
                  return ftx;
               }
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
   public final String getCusip() {
      return this.cusip;
   }

   @NotNull
   public final Party getSecurityBuyer() {
      return this.securityBuyer;
   }

   public CreateAssetTransferRequestInitiatorFlow(@NotNull String cusip, @NotNull Party securityBuyer) {
      Intrinsics.checkParameterIsNotNull(cusip, "cusip");
      Intrinsics.checkParameterIsNotNull(securityBuyer, "securityBuyer");
      super();
      this.cusip = cusip;
      this.securityBuyer = securityBuyer;
      this.progressTracker = Companion.tracker();
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0005\u0005\u0006\u0007\b\tB\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004¨\u0006\n"},
      d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetTransferRequestInitiatorFlow$Companion;", "", "()V", "tracker", "Lnet/corda/core/utilities/ProgressTracker;", "BUILDING", "COLLECTING", "FINALISING", "INITIALISING", "SIGNING", "cordapp-security-seller"}
   )
   public static final class Companion {
      @NotNull
      public final ProgressTracker tracker() {
         return new ProgressTracker(new Step[]{(Step)CreateAssetTransferRequestInitiatorFlow.Companion.INITIALISING.INSTANCE, (Step)CreateAssetTransferRequestInitiatorFlow.Companion.BUILDING.INSTANCE, (Step)CreateAssetTransferRequestInitiatorFlow.Companion.SIGNING.INSTANCE, (Step)CreateAssetTransferRequestInitiatorFlow.Companion.COLLECTING.INSTANCE, (Step)CreateAssetTransferRequestInitiatorFlow.Companion.FINALISING.INSTANCE});
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
         d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetTransferRequestInitiatorFlow$Companion$INITIALISING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-seller"}
      )
      public static final class INITIALISING extends Step {
         public static final CreateAssetTransferRequestInitiatorFlow.Companion.INITIALISING INSTANCE;

         private INITIALISING() {
            super("Performing initial steps.");
            INSTANCE = (CreateAssetTransferRequestInitiatorFlow.Companion.INITIALISING)this;
         }

         static {
            new CreateAssetTransferRequestInitiatorFlow.Companion.INITIALISING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetTransferRequestInitiatorFlow$Companion$BUILDING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-seller"}
      )
      public static final class BUILDING extends Step {
         public static final CreateAssetTransferRequestInitiatorFlow.Companion.BUILDING INSTANCE;

         private BUILDING() {
            super("Building and verifying transaction.");
            INSTANCE = (CreateAssetTransferRequestInitiatorFlow.Companion.BUILDING)this;
         }

         static {
            new CreateAssetTransferRequestInitiatorFlow.Companion.BUILDING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetTransferRequestInitiatorFlow$Companion$SIGNING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-seller"}
      )
      public static final class SIGNING extends Step {
         public static final CreateAssetTransferRequestInitiatorFlow.Companion.SIGNING INSTANCE;

         private SIGNING() {
            super("Signing transaction.");
            INSTANCE = (CreateAssetTransferRequestInitiatorFlow.Companion.SIGNING)this;
         }

         static {
            new CreateAssetTransferRequestInitiatorFlow.Companion.SIGNING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"},
         d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetTransferRequestInitiatorFlow$Companion$COLLECTING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "childProgressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "cordapp-security-seller"}
      )
      public static final class COLLECTING extends Step {
         public static final CreateAssetTransferRequestInitiatorFlow.Companion.COLLECTING INSTANCE;

         @NotNull
         public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.Companion.tracker();
         }

         private COLLECTING() {
            super("Collecting counterparty signature.");
            INSTANCE = (CreateAssetTransferRequestInitiatorFlow.Companion.COLLECTING)this;
         }

         static {
            new CreateAssetTransferRequestInitiatorFlow.Companion.COLLECTING();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016¨\u0006\u0005"},
         d2 = {"Lcom/synechron/cordapp/seller/flows/CreateAssetTransferRequestInitiatorFlow$Companion$FINALISING;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "childProgressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "cordapp-security-seller"}
      )
      public static final class FINALISING extends Step {
         public static final CreateAssetTransferRequestInitiatorFlow.Companion.FINALISING INSTANCE;

         @NotNull
         public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
         }

         private FINALISING() {
            super("Finalising transaction.");
            INSTANCE = (CreateAssetTransferRequestInitiatorFlow.Companion.FINALISING)this;
         }

         static {
            new CreateAssetTransferRequestInitiatorFlow.Companion.FINALISING();
         }
      }
   }
}

