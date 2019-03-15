package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.flows.SignTxFlow;
import com.synechron.cordapp.flows.AbstractCreateAssetTransferRequestFlow;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.transactions.SignedTransaction;
import org.jetbrains.annotations.NotNull;

@InitiatedBy(AbstractCreateAssetTransferRequestFlow.class)
@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0002H\u0017R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"},
   d2 = {"Lcom/synechron/cordapp/buyer/flows/CreateAssetTransferRequestResponderFlow;", "Lnet/corda/core/flows/FlowLogic;", "Lnet/corda/core/transactions/SignedTransaction;", "otherSideSession", "Lnet/corda/core/flows/FlowSession;", "(Lnet/corda/core/flows/FlowSession;)V", "call", "cordapp-security-buyer"}
)
public final class CreateAssetTransferRequestResponderFlow extends FlowLogic {
   private final FlowSession otherSideSession;

   @Suspendable
   @NotNull
   public SignedTransaction call() {
      SignedTransaction stx = (SignedTransaction)this.subFlow((FlowLogic)(new SignTxFlow(this.otherSideSession)));
      return FlowLogic.waitForLedgerCommit$default(this, stx.getId(), false, 2, (Object)null);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object call() {
      return this.call();
   }

   public CreateAssetTransferRequestResponderFlow(@NotNull FlowSession otherSideSession) {
      Intrinsics.checkParameterIsNotNull(otherSideSession, "otherSideSession");
      super();
      this.otherSideSession = otherSideSession;
   }
}

