package com.template.cordapp.common.flows;

import co.paralleluniverse.fibers.Suspendable;

import net.corda.core.contracts.AttachmentResolutionException;
import net.corda.core.contracts.TransactionResolutionException;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.internal.ResolveTransactionsFlow;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.UntrustworthyData;



public final class ReceiveTransactionUnVerifiedFlow extends FlowLogic {
   private final FlowSession otherSideSession;

   @Suspendable
   public SignedTransaction call() throws FlowException {
      FlowSession thisS = this.otherSideSession;
      UntrustworthyData receiveriv = thisS.receive(SignedTransaction.class);
      SignedTransaction it = (SignedTransaction)receiveriv.getFromUntrustedWorld();
      this.subFlow((new ResolveTransactionsFlow(it, this.otherSideSession)));
      return it;
   }


   public ReceiveTransactionUnVerifiedFlow( FlowSession otherSideSession) {
      super();
      this.otherSideSession = otherSideSession;
   }
}

