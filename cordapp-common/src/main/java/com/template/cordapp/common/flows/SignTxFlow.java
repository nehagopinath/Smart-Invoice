package com.template.cordapp.common.flows;

import kotlin.collections.CollectionsKt;
import net.corda.core.contracts.Requirements;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;

import java.security.Signature;
import java.security.SignatureException;
import net.corda.core.utilities.ProgressTracker;

import java.security.SignatureException;


public final class SignTxFlow extends SignTransactionFlow {
   protected void checkTransaction(SignedTransaction stx) throws FlowException  {
      boolean expr= CollectionsKt.any(stx.getSigs());
      if (!expr) {
         throw (new IllegalArgumentException("Failed requirement: Must be signed by the initiator"));
      } else {
         stx.verify(this.getServiceHub(),false);
      }


   }

   public SignTxFlow( FlowSession otherFlow) {
      super(otherFlow,null);
   }
}

