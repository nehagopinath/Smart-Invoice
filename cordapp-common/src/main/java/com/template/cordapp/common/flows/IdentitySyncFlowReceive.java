package com.template.cordapp.common.flows;

import co.paralleluniverse.fibers.Suspendable;

import java.security.InvalidAlgorithmParameterException;
import java.security.SignatureException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.PartyAndCertificate;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.WireTransaction;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.UntrustworthyData;
import net.corda.core.utilities.ProgressTracker.Step;

public final class IdentitySyncFlowReceive extends FlowLogic {

    private final FlowSession otherSideSession;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public IdentitySyncFlowReceive(FlowSession otherSideSessions) {
        this.otherSideSession = otherSideSessions;

    }

    @Override
    public ProgressTracker getProgressTracker() {
        return this.progressTracker;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {

        FlowSession thisSession = this.otherSideSession;
        UntrustworthyData $receiver$iv = thisSession.receive(List.class);
        List it = (List) $receiver$iv.getFromUntrustedWorld();
        //Iterable $receiver$iv = (Iterable) it;
        Collection destination = new ArrayList();
        Iterator itIterator = it.iterator();

        while (itIterator.hasNext()) {
            Object element = itIterator.next();
            AbstractParty itAbParty = (AbstractParty) element;
            if (this.getServiceHub().getIdentityService().wellKnownPartyFromAnonymous(itAbParty) == null) {
                destination.add(element);
            }
        }

        List unknownIdentities = (List) destination;
        //FlowSession this_$iv = this.otherSideSession;
        UntrustworthyData missingIdentities = thisSession.sendAndReceive(List.class, unknownIdentities);
        List identities = (List) missingIdentities.getFromUntrustedWorld();
        //Iterable $receiver$iv = (Iterable) identities;
        Iterator identIterator = identities.iterator();

        while (identIterator.hasNext()) {
            Object element$iv = identIterator.next();
            PartyAndCertificate itPartCert = (PartyAndCertificate) element$iv;
            itPartCert.verify(this.getServiceHub().getIdentityService().getTrustAnchor());
            try {
                this.getServiceHub().getIdentityService().verifyAndRegisterIdentity(itPartCert);
            }
            catch (CertificateExpiredException | CertificateNotYetValidException | InvalidAlgorithmParameterException e)
            {
                e.printStackTrace();
            }
        }

        /*Iterable $receiver$iv = (Iterable) identities;
        Iterator var20 = $receiver$iv.iterator();**/

        /*while (identIterator.hasNext()) {
            Object element$iv = identIterator.next();
            PartyAndCertificate identity = (PartyAndCertificate) element$iv;
        }*/

        //ToDo See what to return
        return null;

    }

    // $FF: synthetic method

    /*@NotNull
    public final FlowSession getOtherSideSession() {
        return this.otherSideSession;
    }

    public Receive(@NotNull FlowSession otherSideSession) {
        Intrinsics.checkParameterIsNotNull(otherSideSession, "otherSideSession");
        super();
        this.otherSideSession = otherSideSession;
        this.progressTracker = new ProgressTracker(new Step[]{(Step)IdentitySyncFlow.Receive.Companion.RECEIVING_IDENTITIES.INSTANCE, (Step)IdentitySyncFlow.Receive.Companion.RECEIVING_CERTIFICATES.INSTANCE});
    }


    public static final class Companion {
        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }*/



    }

