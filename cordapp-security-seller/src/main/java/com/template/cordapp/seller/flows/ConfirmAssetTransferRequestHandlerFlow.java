package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.flows.AbstractConfirmAssetTransferRequestFlow;
import net.corda.confidential.IdentitySyncFlow;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;

@InitiatedBy(AbstractConfirmAssetTransferRequestFlow.class)

public class ConfirmAssetTransferRequestHandlerFlow extends FlowLogic<SignedTransaction> {

   private final FlowSession otherSideSession;


   public ConfirmAssetTransferRequestHandlerFlow(FlowSession otherPartySession)
   {
      this.otherSideSession = otherPartySession;
   }
   @Suspendable
   @Override
   public SignedTransaction call() throws FlowException {

      class SignTxFlow extends SignTransactionFlow {
         private SignTxFlow(FlowSession otherPartySession, ProgressTracker progressTracker) {
            super(otherPartySession, progressTracker);
         }

         @Override
         protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {

         }
      }

      subFlow((new IdentitySyncFlow.Receive(this.otherSideSession)));
      SignedTransaction stx = subFlow(new SignTxFlow(otherSideSession,SignTransactionFlow.Companion.tracker()));
      return waitForLedgerCommit(stx.getId());
   }
}

