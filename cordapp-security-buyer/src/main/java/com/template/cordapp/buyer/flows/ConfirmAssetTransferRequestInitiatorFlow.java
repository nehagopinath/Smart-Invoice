package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.template.cordapp.common.exception.InvalidPartyException;
import com.template.cordapp.common.flows.IdentitySyncFlow;
import com.template.cordapp.contract.AssetTransferContract;
import com.template.cordapp.flows.AbstractConfirmAssetTransferRequestFlow;
import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import com.template.cordapp.state.RequestStatus;
import java.security.PublicKey;
import java.time.Duration;
import java.util.*;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.confidential.SwapIdentitiesFlow;
import net.corda.core.contracts.*;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import static com.template.cordapp.state.RequestStatus.PENDING;

/**
 * The security buyer uses this flow to review and confirm received transaction from seller of security.
 * If everything is okay then `Buyer` party initiate this flow to send received transaction to `Clearing House` for further
 * verification and settlement.
 */

@StartableByRPC

public class ConfirmAssetTransferRequestInitiatorFlow extends AbstractConfirmAssetTransferRequestFlow{

    private final UniqueIdentifier linearId;
    private final Party clearingHouse;

    public ConfirmAssetTransferRequestInitiatorFlow(UniqueIdentifier linearId, Party clearingHouse) {
        this.linearId = linearId;
        this.clearingHouse = clearingHouse;
    }

    private final ProgressTracker.Step SWAP_IDENTITY = new ProgressTracker.Step("Swap Identity");
    private final ProgressTracker.Step INITIALISING = new ProgressTracker.Step("Performing initial steps");
    private final ProgressTracker.Step BUILDING = new ProgressTracker.Step("Building and verifying transaction");
    private final ProgressTracker.Step SIGNING = new ProgressTracker.Step("Signing transaction");
    private final ProgressTracker.Step IDENTITY_SYNC = new ProgressTracker.Step("Sync identities with counter parties") {
        @Override
        public ProgressTracker childProgressTracker() {
            return IdentitySyncFlow.send.Companion.tracker();
        }

    };
    private final ProgressTracker.Step COLLECTING = new ProgressTracker.Step("Collecting counterparty signature") {
        @Override
        public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.Companion.tracker();
        }

    };
    private final ProgressTracker.Step FINALISING = new ProgressTracker.Step("Finalising transaction") {

        @Override
        public ProgressTracker childProgressTracker() { return FinalityFlow.Companion.tracker(); }

    };

    final ProgressTracker progressTracker = new ProgressTracker(
            SWAP_IDENTITY,
            INITIALISING,
            BUILDING,
            SIGNING,
            IDENTITY_SYNC,
            COLLECTING,
            FINALISING
    );

    /**
     * The flow logic is encapsulated within the call() method.
     */
    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {

        progressTracker.setCurrentStep(SWAP_IDENTITY);
        // We retrieve the notary identity from the network map.
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //Swap Identity
        LinkedHashMap txKeys = subFlow(new SwapIdentitiesFlow(clearingHouse));
        boolean size = txKeys.size() == 2;
        if (!size) {
            String illegalState = "Something went wrong when generating confidential identities.";
            throw new IllegalStateException(illegalState);
        }

        AnonymousParty anonymousCustodian = (AnonymousParty) txKeys.get(this.clearingHouse);
        if (anonymousCustodian == null) {
            throw new FlowException("Couldn't create clearing house anonymous identity.");
        }

        progressTracker.setCurrentStep(INITIALISING);

        AnonymousParty anonymousMe = (AnonymousParty) txKeys.get(this.getOurIdentity());

        StateAndRef input = this.loadState(this.getServiceHub(), this.linearId, AssetTransfer.class);

        Collection participants1 = (Collection) ((AssetTransfer) input.getState().getData()).getParticipants();
        Intrinsics.checkExpressionValueIsNotNull(anonymousCustodian, "anonymousCustodian");
        List participants = CollectionsKt.plus(participants1, anonymousCustodian);

        Asset asset = (Asset) input.getState().getData();
        AssetTransfer assetTransfer = (AssetTransfer) input.getState().getData();
        AbstractParty abstractParty = (AbstractParty) anonymousCustodian;
        TransactionBuilder txb = null;
        RequestStatus requestStatus = PENDING;

        AssetTransfer output = new AssetTransfer(asset, null, anonymousMe, anonymousCustodian, PENDING, participants, linearId);

        if (getOurIdentity().getName() != this.resolveIdentity(this.getServiceHub(), output.getSecurityBuyer()).getName()) {
            throw new InvalidPartyException("Flow must be initiated by Lender Of Cash.");
        }


        PublicKey ourSigningKey = output.getSecurityBuyer().getOwningKey();

        final Command<AssetTransferContract.Commands.ConfirmRequest> command = new Command(
                new AssetTransferContract.Commands.ConfirmRequest(),
                ImmutableList.of(assetTransfer.getParticipants()));

        progressTracker.setCurrentStep(BUILDING);

        // We create a transaction builder and add the components.
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addInputState(input)
                .addOutputState(output, AssetTransferContract.ASSET_TRANSFER_CONTRACT_ID)
                .addCommand(command)
                .setTimeWindow(getServiceHub().getClock().instant(), Duration.ofSeconds(60));

        // Signing the transaction.
        progressTracker.setCurrentStep(SIGNING);
        SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder, ourSigningKey);

        Iterable $receiver$iv = (Iterable)participants;
        Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
        Iterator var31 = $receiver$iv.iterator();

        boolean var15;
        Object item$iv$iv;
        while(var31.hasNext()) {
            item$iv$iv = var31.next();
            AbstractParty it = (AbstractParty)item$iv$iv;
            var15 = false;
            Party var38 = this.resolveIdentity(this.getServiceHub(), it);
            destination$iv$iv.add(var38);
        }

        $receiver$iv = (Iterable)((List)destination$iv$iv);
        destination$iv$iv = (Collection)(new ArrayList());
        var31 = $receiver$iv.iterator();

        Party it;
        while(var31.hasNext()) {
            item$iv$iv = var31.next();
            it = (Party)item$iv$iv;
            var15 = false;
            if (Intrinsics.areEqual(it.getName(), this.getOurIdentity().getName()) ^ true) {
                destination$iv$iv.add(item$iv$iv);
            }
        }

        $receiver$iv = (Iterable)((List)destination$iv$iv);
        destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
        var31 = $receiver$iv.iterator();

        while(var31.hasNext()) {
            item$iv$iv = var31.next();
            it = (Party)item$iv$iv;
            var15 = false;
            FlowSession var39 = this.initiateFlow(it);
            destination$iv$iv.add(var39);
        }

        Set<AbstractParty> otherPartySession = CollectionsKt.toSet(destination$iv$iv);

        progressTracker.setCurrentStep(IDENTITY_SYNC);

        this.subFlow(new IdentitySyncFlow.send(otherPartySession,
                txBuilder.toWireTransaction(getServiceHub()),
                IDENTITY_SYNC.childProgressTracker()));

        // Obtaining the counter-party's signature.
        progressTracker.setCurrentStep(COLLECTING);
        final SignedTransaction fullySignedTx = subFlow(
                new CollectSignaturesFlow(signedTx, (Collection)otherPartySession, CollectionsKt.listOf(ourSigningKey), CollectSignaturesFlow.Companion.tracker()));

        // Finalising the transaction.
        progressTracker.setCurrentStep(FINALISING);
        return subFlow(new FinalityFlow(fullySignedTx));


    }
}



