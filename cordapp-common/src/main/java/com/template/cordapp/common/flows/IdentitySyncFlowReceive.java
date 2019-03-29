package com.template.cordapp.common.flows;

import co.paralleluniverse.fibers.Suspendable;
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

    private final Set otherSideSession;
    private final ProgressTracker progressTracker = new ProgressTracker();

    public IdentitySyncFlowReceive(Set otherSideSessions) {
        this.otherSideSession = otherSideSessions;

    }

    @Override
    public ProgressTracker getProgressTracker() {
        return this.progressTracker;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        this.getProgressTracker().setCurrentStep((Step) IdentitySyncFlow.Receive.Companion.RECEIVING_IDENTITIES.INSTANCE);
        Set this_$iv = this.otherSideSession;
        UntrustworthyData $receiver$iv = this_$iv.receive(List.class);
        List it = (List) $receiver$iv.getFromUntrustedWorld();
        Iterable $receiver$iv = (Iterable) it;
        Collection destination$iv$iv = (Collection) (new ArrayList());
        Iterator var6 = $receiver$iv.iterator();

        while (var6.hasNext()) {
            Object element$iv$iv = var6.next();
            AbstractParty it = (AbstractParty) element$iv$iv;
            if (this.getServiceHub().getIdentityService().wellKnownPartyFromAnonymous(it) == null) {
                destination$iv$iv.add(element$iv$iv);
            }
        }

        List unknownIdentities = (List) destination$iv$iv;
        this.getProgressTracker().setCurrentStep((Step) IdentitySyncFlow.Receive.Companion.RECEIVING_CERTIFICATES.INSTANCE);
        FlowSession this_$iv = this.otherSideSession;
        UntrustworthyData missingIdentities = this_$iv.sendAndReceive(List.class, unknownIdentities);
        List identities = (List) missingIdentities.getFromUntrustedWorld();
        Iterable $receiver$iv = (Iterable) identities;
        Iterator var23 = $receiver$iv.iterator();

        while (var23.hasNext()) {
            Object element$iv = var23.next();
            PartyAndCertificate it = (PartyAndCertificate) element$iv;
            it.verify(this.getServiceHub().getIdentityService().getTrustAnchor());
        }

        Iterable $receiver$iv = (Iterable) identities;
        Iterator var20 = $receiver$iv.iterator();

        while (var20.hasNext()) {
            Object element$iv = var20.next();
            PartyAndCertificate identity = (PartyAndCertificate) element$iv;
            this.getServiceHub().getIdentityService().verifyAndRegisterIdentity(identity);
        }

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

