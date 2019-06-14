package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.synechron.cordapp.contract.AssetContract;
import com.template.cordapp.state.Asset;
import net.corda.core.contracts.*;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.time.Duration;

/**
 * Create the [Asset] state on ledger. This state acting as security/bond on ledger which going to be sold for cash.
 */
// ******************
// * Initiator flow *
// ******************
public class CreateAssetStateFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final String cusip;
        private final String assetName;
        private final Amount purchaseCost;

        private final ProgressTracker.Step INITIALISING = new ProgressTracker.Step("Performing initial steps");
        private final ProgressTracker.Step BUILDING = new ProgressTracker.Step("Building and verifying transaction");
        private final ProgressTracker.Step SIGNING = new ProgressTracker.Step("Signing transaction");
        private final ProgressTracker.Step FINALISING = new ProgressTracker.Step("Finalising transaction") {
            @Override
            public ProgressTracker childProgressTracker() {
                return FinalityFlow.Companion.tracker();
            }
        };
        // The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
        // checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call()
        // function.
        private final ProgressTracker progressTracker = new ProgressTracker(
                INITIALISING,
                BUILDING,
                SIGNING,
                FINALISING
        );

        public Initiator(String cusip, String assetName, Amount purchaseCost) {
            this.cusip = cusip;
            this.assetName = assetName;
            this.purchaseCost = purchaseCost;
        }

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }


        /**
         * The flow logic is encapsulated within the call() method.
         */
        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            final Command<AssetContract.Commands.Create> command = new Command<>(new AssetContract.Commands.Create(), getOurIdentity().getOwningKey());

            progressTracker.setCurrentStep(INITIALISING);

            System.out.println(getOurIdentity());

            Asset asset = new Asset(cusip, assetName, purchaseCost, getOurIdentity());

            progressTracker.setCurrentStep(BUILDING);

            TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(asset, AssetContract.ASSET_CONTRACT_ID)
                    .addCommand(command)
                    .setTimeWindow(getServiceHub().getClock().instant(), Duration.ofSeconds(30));

            progressTracker.setCurrentStep(SIGNING);
            SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

            progressTracker.setCurrentStep(FINALISING);
            SignedTransaction finalTxn;
            finalTxn = subFlow(new FinalityFlow(signedTx, FINALISING.childProgressTracker()));
            return finalTxn;

        }
    }
}

