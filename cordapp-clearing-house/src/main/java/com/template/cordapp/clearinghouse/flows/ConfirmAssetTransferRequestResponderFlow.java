package com.template.cordapp.clearinghouse.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.buyer.flows.ConfirmAssetTransferRequestInitiatorFlow;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.common.flows.IdentitySyncFlowReceive;
import net.corda.confidential.IdentitySyncFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.transactions.SignedTransaction;


@InitiatedBy(ConfirmAssetTransferRequestInitiatorFlow.class)

public final class ConfirmAssetTransferRequestResponderFlow extends FlowLogic {
   private final FlowSession otherSideSession;

   @Suspendable
   public SignedTransaction call() throws FlowException {
      this.subFlow((new IdentitySyncFlow.Receive(this.otherSideSession)));
      SignedTransaction stx = (SignedTransaction)this.subFlow((new SignTxFlow(this.otherSideSession)));
      return waitForLedgerCommit(stx.getId());
   }

   public ConfirmAssetTransferRequestResponderFlow(FlowSession otherSideSession) {
      super();
      this.otherSideSession = otherSideSession;
   }
}

