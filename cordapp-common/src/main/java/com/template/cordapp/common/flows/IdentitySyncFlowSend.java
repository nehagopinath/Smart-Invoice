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
import net.corda.core.utilities.ProgressTracker.Step;;

public final class IdentitySyncFlowSend extends FlowLogic {

    private final Set otherSideSessions;
    private final WireTransaction tx;
    private final ProgressTracker progressTracker=new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return this.progressTracker;
    }

    public IdentitySyncFlowSend (Set otherSideSessions, WireTransaction tx)
    {
        this.otherSideSessions=otherSideSessions;
        this.tx=tx;
    }



    @Suspendable
    public SignedTransaction call() throws FlowException {

        Iterable $receiver$iv = this.tx.getInputs();
        Collection destination = new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10));
        Iterator var5 = $receiver$iv.iterator();

        Object item;
        while(var5.hasNext()) {
            item = var5.next();
            StateRef it = (StateRef)item;
            TransactionState ts = this.getServiceHub().loadState(it);
            destination.add(ts);
        }

        $receiver$iv = (Iterable)CollectionsKt.requireNoNulls((List)destination);
        //destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
        var5 = $receiver$iv.iterator();

        TransactionState it;
        while(var5.hasNext()) {
            item = var5.next();
            it = (TransactionState)item$iv$iv;
            ContractState var54 = it.getData();
            destination$iv$iv.add(var54);
        }

        Collection var10000 = (Collection)((List)destination$iv$iv);
        $receiver$iv = (Iterable)this.tx.getOutputs();
        Collection var25 = var10000;
        destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
        var5 = $receiver$iv.iterator();

        while(var5.hasNext()) {
            item$iv$iv = var5.next();
            it = (TransactionState)item$iv$iv;
            ContractState var27 = it.getData();
            destination$iv$iv.add(var27);
        }

        List var55 = (List)destination$iv$iv;
        List states = CollectionsKt.plus(var25, (Iterable)var55);
        Iterable $receiver$iv = (Iterable)states;
        Collection destination$iv$iv = (Collection)(new ArrayList());
        Iterator var34 = $receiver$iv.iterator();

        Iterable $receiver$iv;
        Object element$iv;
        while(var34.hasNext()) {
            element$iv = var34.next();
            ContractState it = (ContractState)element$iv;
            $receiver$iv = (Iterable)it.getParticipants();
            CollectionsKt.addAll(destination$iv$iv, $receiver$iv);
        }

        Set identities = CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
        Iterable $receiver$iv = (Iterable)identities;
        Collection destination$iv$iv = (Collection)(new ArrayList());
        Iterator var38 = $receiver$iv.iterator();

        while(var38.hasNext()) {
            Object element$iv$iv = var38.next();
            AbstractParty it = (AbstractParty)element$iv$iv;
            if (this.getServiceHub().getNetworkMapCache().getNodesByLegalIdentityKey(it.getOwningKey()).isEmpty()) {
                destination$iv$iv.add(element$iv$iv);
            }
        }

        List confidentialIdentities = CollectionsKt.toList((Iterable)((List)destination$iv$iv));
        Iterable $receiver$iv = (Iterable)identities;
        Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
        Iterator var41 = $receiver$iv.iterator();

        while(var41.hasNext()) {
            Object item$iv$iv = var41.next();
            AbstractParty it = (AbstractParty)item$iv$iv;
            Pair var56 = new Pair(it, this.getServiceHub().getIdentityService().certificateFromKey(it.getOwningKey()));
            destination$iv$iv.add(var56);
        }

        Map identityCertificates = MapsKt.toMap((Iterable)((List)destination$iv$iv));
        $receiver$iv = (Iterable)this.otherSideSessions;
        var34 = $receiver$iv.iterator();

        while(var34.hasNext()) {
            element$iv = var34.next();
            FlowSession otherSideSession = (FlowSession)element$iv;
            UntrustworthyData $receiver$iv = otherSideSession.sendAndReceive(List.class, confidentialIdentities);
            List req = (List)$receiver$iv.getFromUntrustedWorld();
            Iterable $receiver$iv = (Iterable)req;
            boolean var53;
            if ($receiver$iv instanceof Collection && ((Collection)$receiver$iv).isEmpty()) {
                var53 = true;
            } else {
                Iterator var12 = $receiver$iv.iterator();

                while(true) {
                    if (!var12.hasNext()) {
                        var53 = true;
                        break;
                    }

                    Object element$iv = var12.next();
                    AbstractParty it = (AbstractParty)element$iv;
                    if (!identityCertificates.keySet().contains(it)) {
                        var53 = false;
                        break;
                    }
                }
            }

            boolean var48 = var53;
            if (!var48) {
                String var51 = "" + otherSideSession.getCounterparty() + " requested a confidential identity not part of transaction: " + this.tx.getId();
                throw (Throwable)(new IllegalArgumentException(var51.toString()));
            }

            $receiver$iv = (Iterable)req;
            Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
            Iterator var50 = $receiver$iv.iterator();

            while(var50.hasNext()) {
                Object item$iv$iv = var50.next();
                AbstractParty it = (AbstractParty)item$iv$iv;
                PartyAndCertificate identityCertificate = (PartyAndCertificate)identityCertificates.get(it);
                if (identityCertificate == null) {
                    throw new IllegalStateException("Counterparty requested a confidential identity for which we do not have the certificate path: " + this.tx.getId());
                }

                destination$iv$iv.add(identityCertificate);
            }

            List sendIdentities = (List)destination$iv$iv;
            otherSideSession.send(sendIdentities);
        }

    }
}
