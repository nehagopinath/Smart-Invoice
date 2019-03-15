package com.synechron.cordapp.common.flows;

import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.Requirements;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0014¨\u0006\t"},
   d2 = {"Lcom/synechron/cordapp/common/flows/SignTxFlow;", "Lnet/corda/core/flows/SignTransactionFlow;", "otherFlow", "Lnet/corda/core/flows/FlowSession;", "(Lnet/corda/core/flows/FlowSession;)V", "checkTransaction", "", "stx", "Lnet/corda/core/transactions/SignedTransaction;", "cordapp-common"}
)
public final class SignTxFlow extends SignTransactionFlow {
   protected void checkTransaction(@NotNull SignedTransaction stx) {
      Intrinsics.checkParameterIsNotNull(stx, "stx");
      Requirements $receiver = Requirements.INSTANCE;
      String $receiver$iv = "Must be signed by the initiator.";
      boolean expr$iv = CollectionsKt.any((Iterable)stx.getSigs());
      if (!expr$iv) {
         throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
      } else {
         stx.verify(this.getServiceHub(), false);
      }
   }

   public SignTxFlow(@NotNull FlowSession otherFlow) {
      Intrinsics.checkParameterIsNotNull(otherFlow, "otherFlow");
      super(otherFlow, (ProgressTracker)null, 2, (DefaultConstructorMarker)null);
   }
}

