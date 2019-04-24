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

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public ConfirmAssetTransferRequestInitiatorFlow(UniqueIdentifier linearId, Party clearingHouse) {
        this.linearId = linearId;
        this.clearingHouse = clearingHouse;
    }

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

        AnonymousParty anonymousMe = (AnonymousParty) txKeys.get(this.getOurIdentity());

        progressTracker.setCurrentStep(INITIALISING);

        StateAndRef<AssetTransfer> input = this.loadState(this.getServiceHub(), this.linearId, AssetTransfer.class);

        Collection participants1 = input.getState().getData().getParticipants();
        Intrinsics.checkExpressionValueIsNotNull(anonymousCustodian, "anonymousCustodian");
        List participants = CollectionsKt.plus(participants1, anonymousCustodian);

       AssetTransfer assetTransfer = input.getState().getData().copy(null,null,anonymousMe,anonymousCustodian,PENDING,participants,linearId);

        if (getOurIdentity().getName() != this.resolveIdentity(this.getServiceHub(), assetTransfer.getSecurityBuyer()).getName()) {
            throw new InvalidPartyException("Flow must be initiated by Lender Of Cash.");
        }

        progressTracker.setCurrentStep(BUILDING);

        PublicKey ourSigningKey = assetTransfer.getSecurityBuyer().getOwningKey();

        List<PublicKey> requiredSigners = Arrays.asList(getOurIdentity().getOwningKey(), clearingHouse.getOwningKey());

        final Command<AssetTransferContract.Commands.ConfirmRequest> command = new Command(
                new AssetTransferContract.Commands.ConfirmRequest(),requiredSigners);

        // We create a transaction builder and add the components.
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addInputState(input)
                .addOutputState(assetTransfer, AssetTransferContract.ASSET_TRANSFER_CONTRACT_ID)
                .addCommand(command)
                .setTimeWindow(getServiceHub().getClock().instant(), Duration.ofSeconds(60));

        // Signing the transaction.
        progressTracker.setCurrentStep(SIGNING);
        SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder, ourSigningKey);

        Iterable participants_iterable = participants;
        Collection otherSideSession = new ArrayList(CollectionsKt.collectionSizeOrDefault(participants_iterable, 10));
        Iterator participant = participants_iterable.iterator();

        boolean value;
        Object nextItem;
        while(participant.hasNext()) {
            nextItem = participant.next();
            AbstractParty it = (AbstractParty)nextItem;
            value = false;
            Party party = this.resolveIdentity(this.getServiceHub(), it);
            otherSideSession.add(party);
        }

        participants_iterable = otherSideSession;
        otherSideSession = new ArrayList();
        participant = participants_iterable.iterator();

        Party it;
        while(participant.hasNext()) {
            nextItem = participant.next();
            it = (Party)nextItem;
            value = false;
            if (Intrinsics.areEqual(it.getName(), this.getOurIdentity().getName()) ^ true) {
                otherSideSession.add(nextItem);
            }
        }

        participants_iterable = otherSideSession;
        otherSideSession = new ArrayList(CollectionsKt.collectionSizeOrDefault(participants_iterable, 10));
        participant = participants_iterable.iterator();

        while(participant.hasNext()) {
            nextItem = participant.next();
            it = (Party)nextItem;
            value = false;
            FlowSession flowSession = this.initiateFlow(it);
            otherSideSession.add(flowSession);
        }

        Set<AbstractParty> otherPartySession = CollectionsKt.toSet(otherSideSession);

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



