package com.template.cordapp.clearinghouse.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.buyer.flows.ConfirmAssetTransferRequestInitiatorFlow;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.flows.AbstractConfirmAssetTransferRequestFlow;
import net.corda.confidential.IdentitySyncFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.transactions.SignedTransaction;


@InitiatedBy(AbstractConfirmAssetTransferRequestFlow.class)

public final class ConfirmAssetTransferRequestResponderFlow extends FlowLogic<SignedTransaction> {
   private final FlowSession otherSideSession;

   @Suspendable
   public SignedTransaction call() throws FlowException {
      this.subFlow((new IdentitySyncFlow.Receive(this.otherSideSession)));
      SignedTransaction stx = this.subFlow((new SignTxFlow(this.otherSideSession)));
      return waitForLedgerCommit(stx.getId());
   }

   public ConfirmAssetTransferRequestResponderFlow(FlowSession otherSideSession) {

      //todo 2: does it need super ?
      super();
      this.otherSideSession = otherSideSession;
   }
}

