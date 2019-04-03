package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.common.flows.IdentitySyncFlow.Receive;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.transactions.SignedTransaction;
import com.template.cordapp.buyer.flows.ConfirmAssetTransferRequestInitiatorFlow;


@InitiatedBy(ConfirmAssetTransferRequestInitiatorFlow.class)

public class ConfirmAssetTransferRequestHandlerFlow extends FlowLogic<SignedTransaction> {
   private final FlowSession otherSideSession;

   public ConfirmAssetTransferRequestHandlerFlow(FlowSession otherPartySession)
   {
      this.otherSideSession = otherPartySession;
   }
   @Suspendable
   public SignedTransaction call() throws FlowException {
      this.subFlow((new Receive(this.otherSideSession)));

      SignedTransaction stx = (SignedTransaction)this.subFlow((FlowLogic)(new SignTxFlow(this.otherSideSession)));

      return waitForLedgerCommit(stx.getId());
   }


}

