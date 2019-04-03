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

public final class IdentitySyncFlowSend extends FlowLogic {

    private final Set otherSideSessions;
    private final WireTransaction tx;
    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return this.progressTracker;
    }

    public IdentitySyncFlowSend(Set otherSideSessions, WireTransaction tx) {
        this.otherSideSessions = otherSideSessions;
        this.tx = tx;
    }

    @Override
    @Suspendable
    public void call() throws FlowException {

        //1. All states
        Iterable $receiver$iv = (Iterable) this.tx.getInputs();

        int $receiver$iv$size = ((Collection<?>) $receiver$iv).size();
        Collection destination = new ArrayList($receiver$iv$size);

        Iterator var5 = $receiver$iv.iterator();

        Object item$iv$iv;
        while (var5.hasNext()) {
            item$iv$iv = var5.next();
            StateRef it = (StateRef) item$iv$iv;
            TransactionState var26 = this.getServiceHub().loadState(it);
            destination.add(var26);
        }

        $receiver$iv = (Iterable) CollectionsKt.requireNoNulls((List) destination);
        destination = (Collection) (new ArrayList(((Collection<?>) $receiver$iv).size()));
        var5 = $receiver$iv.iterator();

        TransactionState it;
        while (var5.hasNext()) {
            item$iv$iv = var5.next();
            it = (TransactionState) item$iv$iv;
            ContractState var54 = it.getData();
            destination.add(var54);
        }

        Collection var10000 = (Collection) ((List) destination);
        $receiver$iv = (Iterable) this.tx.getOutputs();
        Collection var25 = var10000;
        destination = (Collection) (new ArrayList(((Collection<?>) $receiver$iv).size()));
        var5 = $receiver$iv.iterator();

        while (var5.hasNext()) {
            item$iv$iv = var5.next();
            it = (TransactionState) item$iv$iv;
            ContractState var27 = it.getData();
            destination.add(var27);
        }

        List var55 = (List) destination;
        List states = CollectionsKt.plus(var25, (Iterable) var55);
        $receiver$iv = (Iterable) states;
        destination = (Collection) (new ArrayList(((Collection<?>) $receiver$iv).size()));
        Iterator var34 = $receiver$iv.iterator();

        Object element$iv;
        while (var34.hasNext()) {
            element$iv = var34.next();
            ContractState itContractState = (ContractState) element$iv;
            $receiver$iv = (Iterable) itContractState.getParticipants();
            CollectionsKt.addAll(destination, $receiver$iv);
        }

        //2. all the identities
        Set identities = CollectionsKt.toSet((Iterable) ((List) destination));
        $receiver$iv = (Iterable) identities;
        destination = (Collection) (new ArrayList(((Collection<?>) $receiver$iv).size()));
        Iterator var38 = $receiver$iv.iterator();

        while (var38.hasNext()) {
            Object element$iv$iv = var38.next();
            AbstractParty itAbstractParty = (AbstractParty) element$iv$iv;
            if (this.getServiceHub().getNetworkMapCache().getNodesByLegalIdentityKey(itAbstractParty.getOwningKey()).isEmpty()) {
                destination.add(element$iv$iv);
            }
        }

        // 3. confidential identities
        List confidentialIdentities = CollectionsKt.toList((Iterable) ((List) destination));
        $receiver$iv = (Iterable) identities;
        destination = (Collection) (new ArrayList(((Collection<?>) $receiver$iv).size()));
        Iterator var41 = $receiver$iv.iterator();

        while (var41.hasNext()) {
            item$iv$iv = var41.next();
            AbstractParty itAbstractParty = (AbstractParty) item$iv$iv;
            Pair var56 = new Pair(itAbstractParty, this.getServiceHub().getIdentityService().certificateFromKey(itAbstractParty.getOwningKey()));
            destination.add(var56);
        }

        // 4. identity certificates

        Map identityCertificates = MapsKt.toMap((Iterable) ((List) destination));
        $receiver$iv = (Iterable) this.otherSideSessions;
        var34 = $receiver$iv.iterator();

        while (var34.hasNext()) {
            element$iv = var34.next();
            FlowSession otherSideSession = (FlowSession) element$iv;
            UntrustworthyData unTrustworthyWrapper = otherSideSession.sendAndReceive(List.class, confidentialIdentities);
            /*
             TODO : very very important fix. this method here actually is critical part of validates the aunthenticity
             of the trasaction, but the kotlin internal library method is inaccessable here,
             there are multiple such usages in the whole project
             its best if we can write a duplicate code doing this validation.
             or at least try find a way to access this
             kotlin method. I tried my best to fix but could not successfully do it :(
              */

            List req = (List) unTrustworthyWrapper.getFromUntrustedWorld();
            $receiver$iv = (Iterable) req;
            boolean var53;
            if ($receiver$iv instanceof Collection && ((Collection) $receiver$iv).isEmpty()) {
                var53 = true;
            } else {
                Iterator var12 = $receiver$iv.iterator();

                while (true) {
                    if (!var12.hasNext()) {
                        var53 = true;
                        break;
                    }

                    element$iv = var12.next();
                    AbstractParty itAbstractParty = (AbstractParty) element$iv;
                    if (!identityCertificates.keySet().contains(itAbstractParty)) {
                        var53 = false;
                        break;
                    }
                }
            }

            boolean var48 = var53;
            if (!var48) {
                String var51 = "" + otherSideSession.getCounterparty() + " requested a confidential identity not part of transaction: " + this.tx.getId();
                throw (new IllegalArgumentException(var51.toString()));
            }

            $receiver$iv = (Iterable) req;
            destination = (Collection) (new ArrayList(((Collection<?>) $receiver$iv).size()));
            Iterator var50 = $receiver$iv.iterator();

            while (var50.hasNext()) {
                item$iv$iv = var50.next();
                AbstractParty itAbstractParty = (AbstractParty) item$iv$iv;
                PartyAndCertificate identityCertificate = (PartyAndCertificate) identityCertificates.get(itAbstractParty);
                if (identityCertificate == null) {
                    throw (new IllegalStateException("Counterparty requested a confidential identity for which we do not have the certificate path: " + this.tx.getId()));
                }

                destination.add(identityCertificate);
            }

            List sendIdentities = (List) destination;
            otherSideSession.send(sendIdentities);
        }
    }
}

