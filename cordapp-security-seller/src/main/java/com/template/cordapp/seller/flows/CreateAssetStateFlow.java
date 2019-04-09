package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.contract.AssetContract;
import com.template.cordapp.flows.FlowLogicCommonMethods;
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
@InitiatingFlow
@StartableByRPC
public class CreateAssetStateFlow extends FlowLogic<SignedTransaction> {
    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();
    private final String cusip;
    private final String assetName;
    private final Amount purchaseCost;


    public CreateAssetStateFlow(String cusip, String assetName, Amount purchaseCost) {
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
        // We retrieve the notary identity from the network map.
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // We create the transaction components.
        System.out.println("Initializing");
        Asset asset = new Asset(cusip, assetName, purchaseCost, getOurIdentity());

        Command command = new Command<>(new AssetContract.Commands.Create(), getOurIdentity().getOwningKey());

        System.out.println("Now building");
        // We create a transaction builder and add the components.
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(asset, AssetContract.ASSET_CONTRACT_ID)
                .addCommand(command)
                .setTimeWindow(getServiceHub().getClock().instant(), Duration.ofSeconds(30));

        // Signing the transaction.
        System.out.println("Signing the transaction.");
        SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Finalising the transaction.
        System.out.println("Finalising the transaction");
        SignedTransaction finalTxn =  subFlow(new FinalityFlow(signedTx));

        System.out.println("Finalized transaction.");
        return finalTxn;

    }
}


