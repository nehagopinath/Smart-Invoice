package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.flows.AbstractConfirmAssetTransferRequestFlow;
import net.corda.confidential.IdentitySyncFlow;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;

@InitiatedBy(AbstractConfirmAssetTransferRequestFlow.class)

public class ConfirmAssetTransferRequestHandlerFlow extends FlowLogic<SignedTransaction> {
   private final FlowSession otherSideSession;



   private final ProgressTracker.Step COLLECTING = new ProgressTracker.Step("Sync identities with counter parties") {
         @Override
         public ProgressTracker childProgressTracker() {
            return SignTransactionFlow.Companion.tracker();
         }
   };


   /**
    * The progress tracker provides checkpoints indicating the progress of the flow to observers.
    */

   private final ProgressTracker progressTracker = new ProgressTracker(
           COLLECTING
   );

   @Override
   public ProgressTracker getProgressTracker() {
      return progressTracker;
   }

   public ConfirmAssetTransferRequestHandlerFlow(FlowSession otherPartySession)
   {
      this.otherSideSession = otherPartySession;
   }
   @Suspendable
   public SignedTransaction call() throws FlowException {

      progressTracker.setCurrentStep(COLLECTING);

      this.subFlow((new IdentitySyncFlow.Receive(this.otherSideSession)));

      SignedTransaction stx = subFlow(new SignTxFlow(this.otherSideSession));

      return waitForLedgerCommit(stx.getId());
   }
}

