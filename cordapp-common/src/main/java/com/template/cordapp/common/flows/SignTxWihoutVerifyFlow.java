package com.template.cordapp.common.flows;


import kotlin.jvm.internal.Intrinsics;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;


public final class SignTxWihoutVerifyFlow extends SignTransactionFlow {
   protected void checkTransaction( SignedTransaction stx) {
      Intrinsics.checkParameterIsNotNull(stx, "stx");
   }

   public SignTxWihoutVerifyFlow( FlowSession otherFlow) {
      super(otherFlow, null);
   }
}

