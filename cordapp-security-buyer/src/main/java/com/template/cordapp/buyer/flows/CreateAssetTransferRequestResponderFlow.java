package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.flows.AbstractCreateAssetTransferRequestFlow;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatedBy(AbstractCreateAssetTransferRequestFlow.class)

public class CreateAssetTransferRequestResponderFlow extends FlowLogic<SignedTransaction> {

   private final FlowSession otherPartySession;

   public CreateAssetTransferRequestResponderFlow(FlowSession otherPartySession)
   {
      this.otherPartySession = otherPartySession;
   }

   @Suspendable
   @Override
   public SignedTransaction call() throws FlowException
   {
      class SignTxFlow extends SignTransactionFlow {
         private SignTxFlow(FlowSession otherPartySession, ProgressTracker progressTracker) {
            super(otherPartySession, progressTracker);
         }

         @Override
         protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {

         }
      }
      SignedTransaction stx = subFlow(new SignTxFlow(otherPartySession,SignTransactionFlow.Companion.tracker()));
      return waitForLedgerCommit(stx.getId());

   }
}




