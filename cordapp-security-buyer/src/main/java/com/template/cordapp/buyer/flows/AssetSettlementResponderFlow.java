package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.exception.TooManyStatesFoundException;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.common.flows.IdentitySyncFlow.Receive;
import com.synechron.cordapp.flows.AbstractAssetSettlementFlow;
import com.synechron.cordapp.state.AssetTransfer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.confidential.IdentitySyncFlow.Send;
import net.corda.core.contracts.PrivacySalt;
import net.corda.core.contracts.TimeWindow;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.ReceiveTransactionFlow;
import net.corda.core.flows.SendTransactionFlow;
import net.corda.core.node.StatesToRecord;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.UntrustworthyData;
import net.corda.core.utilities.ProgressTracker.Step;
import net.corda.finance.contracts.asset.Cash;
import org.jetbrains.annotations.NotNull;

@InitiatedBy(AbstractAssetSettlementFlow.class)
@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u0000 \r2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\rB\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\b\u0010\f\u001a\u00020\u0002H\u0017R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u000e"},
   d2 = {"Lcom/synechron/cordapp/buyer/flows/AssetSettlementResponderFlow;", "Lnet/corda/core/flows/FlowLogic;", "Lnet/corda/core/transactions/SignedTransaction;", "otherSideSession", "Lnet/corda/core/flows/FlowSession;", "(Lnet/corda/core/flows/FlowSession;)V", "getOtherSideSession", "()Lnet/corda/core/flows/FlowSession;", "progressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "getProgressTracker", "()Lnet/corda/core/utilities/ProgressTracker;", "call", "Companion", "cordapp-security-buyer"}
)
public final class AssetSettlementResponderFlow extends FlowLogic {
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
      this.getProgressTracker().setCurrentStep((Step)AssetSettlementResponderFlow.Companion.ADD_CASH.INSTANCE);
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
         FlowSession this_$iv = this.otherSideSession;
         UntrustworthyData $receiver$iv = this_$iv.receive(UUID.class);
         UUID it = (UUID)$receiver$iv.getFromUntrustedWorld();
         Pair var15 = net.corda.finance.contracts.asset.Cash.Companion.generateSpend$default(Cash.Companion, this.getServiceHub(), new TransactionBuilder(ltx1.getNotary(), it, (List)null, (List)null, (List)null, (List)null, (TimeWindow)null, (PrivacySalt)null, 252, (DefaultConstructorMarker)null), assetTransfer.getAsset().getPurchaseCost(), this.getOurIdentityAndCert(), assetTransfer.getSecuritySeller(), (Set)null, 32, (Object)null);
         TransactionBuilder txbWithCash = (TransactionBuilder)var15.component1();
         List cashSignKeys = (List)var15.component2();
         SignedTransaction ptx2 = this.getServiceHub().signInitialTransaction(txbWithCash);
         this.subFlow((FlowLogic)(new Send(this.otherSideSession, ptx2.getTx())));
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
      this.progressTracker = new ProgressTracker(new Step[]{(Step)AssetSettlementResponderFlow.Companion.ADD_CASH.INSTANCE, (Step)AssetSettlementResponderFlow.Companion.SYNC_IDENTITY.INSTANCE});
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0002\u0003\u0004B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0005"},
      d2 = {"Lcom/synechron/cordapp/buyer/flows/AssetSettlementResponderFlow$Companion;", "", "()V", "ADD_CASH", "SYNC_IDENTITY", "cordapp-security-buyer"}
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
         d2 = {"Lcom/synechron/cordapp/buyer/flows/AssetSettlementResponderFlow$Companion$ADD_CASH;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-buyer"}
      )
      public static final class ADD_CASH extends Step {
         public static final AssetSettlementResponderFlow.Companion.ADD_CASH INSTANCE;

         private ADD_CASH() {
            super("Add cash states.");
            INSTANCE = (AssetSettlementResponderFlow.Companion.ADD_CASH)this;
         }

         static {
            new AssetSettlementResponderFlow.Companion.ADD_CASH();
         }
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
         d2 = {"Lcom/synechron/cordapp/buyer/flows/AssetSettlementResponderFlow$Companion$SYNC_IDENTITY;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-security-buyer"}
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

