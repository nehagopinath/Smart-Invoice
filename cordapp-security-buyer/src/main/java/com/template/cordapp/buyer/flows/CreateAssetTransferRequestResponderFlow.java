package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.flows.SignTxFlow;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;



//todo: check how to add abstract class in @initiatedBy
//@InitiatedBy(AbstractCreateAssetTransferRequestFlow.class)

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
      SignedTransaction stx = subFlow(new SignTxFlow(otherPartySession));
      return waitForLedgerCommit(stx.getId());

   }
}



