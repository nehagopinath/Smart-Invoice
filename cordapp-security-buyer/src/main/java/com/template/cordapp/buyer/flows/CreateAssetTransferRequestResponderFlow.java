package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.flows.AbstractCreateAssetTransferRequestFlow;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(AbstractCreateAssetTransferRequestFlow.class)

class CreateAssetTransferRequestResponderFlow extends FlowLogic<SignedTransaction> {

   private final FlowSession otherPartySession;

   public CreateAssetTransferRequestResponderFlow(FlowSession otherPartySession)
   {
      this.otherPartySession = otherPartySession;
   }

   @Suspendable
   @Override
   public SignedTransaction call() throws FlowException
   {
      SignedTransaction stx = subFlow(new SignTxFlow(otherPartySession));
      return waitForLedgerCommit(stx.getId());

   }
}



