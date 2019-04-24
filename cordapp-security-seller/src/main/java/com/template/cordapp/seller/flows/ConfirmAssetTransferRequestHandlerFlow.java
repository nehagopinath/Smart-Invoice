package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.flows.AbstractConfirmAssetTransferRequestFlow;
import net.corda.confidential.IdentitySyncFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.transactions.SignedTransaction;

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
      this.subFlow((new IdentitySyncFlow.Receive(this.otherSideSession)));
      getLogger().info("========= Responder flow Seller : othersideSession");
      getLogger().info(this.otherSideSession.toString());

      SignedTransaction stx = (SignedTransaction)this.subFlow((FlowLogic)(new SignTxFlow(this.otherSideSession)));
      getLogger().info("========= Responder flow Seller : signed tx");
      getLogger().info(stx.toString());

      return waitForLedgerCommit(stx.getId());
   }


}

