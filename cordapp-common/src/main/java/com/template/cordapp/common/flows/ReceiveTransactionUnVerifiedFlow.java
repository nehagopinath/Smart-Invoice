package com.template.cordapp.common.flows;

import co.paralleluniverse.fibers.Suspendable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.AttachmentResolutionException;
import net.corda.core.contracts.TransactionResolutionException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.internal.ResolveTransactionsFlow;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.UntrustworthyData;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0002H\u0017R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"},
   d2 = {"Lcom/synechron/cordapp/common/flows/ReceiveTransactionUnVerifiedFlow;", "Lnet/corda/core/flows/FlowLogic;", "Lnet/corda/core/transactions/SignedTransaction;", "otherSideSession", "Lnet/corda/core/flows/FlowSession;", "(Lnet/corda/core/flows/FlowSession;)V", "call", "cordapp-common"}
)
public final class ReceiveTransactionUnVerifiedFlow extends FlowLogic {
   private final FlowSession otherSideSession;

   @Suspendable
   @NotNull
   public SignedTransaction call() throws AttachmentResolutionException, TransactionResolutionException {
      FlowSession this_$iv = this.otherSideSession;
      UntrustworthyData $receiver$iv = this_$iv.receive(SignedTransaction.class);
      SignedTransaction it = (SignedTransaction)$receiver$iv.getFromUntrustedWorld();
      this.subFlow((FlowLogic)(new ResolveTransactionsFlow(it, this.otherSideSession)));
      return it;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object call() {
      return this.call();
   }

   public ReceiveTransactionUnVerifiedFlow(@NotNull FlowSession otherSideSession) {
      Intrinsics.checkParameterIsNotNull(otherSideSession, "otherSideSession");
      super();
      this.otherSideSession = otherSideSession;
   }
}

