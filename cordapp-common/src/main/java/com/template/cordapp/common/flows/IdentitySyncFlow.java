package com.template.cordapp.common.flows;

import co.paralleluniverse.fibers.Suspendable;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.*;

import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowLogic.Companion;
import net.corda.core.flows.FlowSession;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.PartyAndCertificate;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.WireTransaction;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.UntrustworthyData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class IdentitySyncFlow {
    public static final IdentitySyncFlow INSTANCE;

    private IdentitySyncFlow() {
    }

    static {
        IdentitySyncFlow var0 = new IdentitySyncFlow();
        INSTANCE = var0;
    }


    public static final class send extends FlowLogic {

        @NotNull
        private final Set otherSideSessions;
        @NotNull
        private final WireTransaction tx;

        @NotNull
        private final ProgressTracker progressTracker;

        public static final IdentitySyncFlow.send.Companion Companion = new IdentitySyncFlow.send.Companion();

        public send(@NotNull Set otherSideSessions, @NotNull WireTransaction tx, @NotNull ProgressTracker progressTracker) {
            super();
            Intrinsics.checkParameterIsNotNull(otherSideSessions, "otherSideSessions");
            Intrinsics.checkParameterIsNotNull(tx, "tx");
            Intrinsics.checkParameterIsNotNull(progressTracker, "progressTracker");
            this.otherSideSessions = otherSideSessions;
            this.tx = tx;
            this.progressTracker = progressTracker;
        }

        public send(@NotNull Set otherSideSessions, @NotNull WireTransaction tx) {
            this(SetsKt.setOf(otherSideSessions), tx, Companion.tracker());
            Intrinsics.checkParameterIsNotNull(otherSideSessions, "otherSideSessions");
            Intrinsics.checkParameterIsNotNull(tx, "tx");


        }

        @NotNull
        public ProgressTracker getProgressTracker() {
            return this.progressTracker;
        }

        @NotNull
        public final Set getOtherSideSessions() {
            return this.otherSideSessions;
        }

        @NotNull
        public final WireTransaction getTx() {
            return this.tx;
        }


        @Override
        @Suspendable
        public Object call() throws FlowException {

            //1. All states

            this.getProgressTracker().setCurrentStep(new Companion.IDENTITY_SYNC());
            Iterable $receiver$iv = (Iterable) this.tx.getInputs();

            int $receiver$iv$size = ((Collection<?>) $receiver$iv).size();
            Collection destination = new ArrayList($receiver$iv$size);

            Iterator var5 = $receiver$iv.iterator();

            Object item$iv$iv = null;
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
            return item$iv$iv;
        }

            public static final class Companion {
                @NotNull
                public final ProgressTracker tracker() {
                    return new ProgressTracker(new ProgressTracker.Step[]{IDENTITY_SYNC.INSTANCE});
                }

                private Companion() {
                }

                public static final class IDENTITY_SYNC extends ProgressTracker.Step {
                    public static final IdentitySyncFlow.send.Companion.IDENTITY_SYNC INSTANCE;

                    private IDENTITY_SYNC() {
                        super("Syncing identities");
                    }

                    static {
                        IdentitySyncFlow.send.Companion.IDENTITY_SYNC var0 = new IdentitySyncFlow.send.Companion.IDENTITY_SYNC();
                        INSTANCE = var0;
                    }
                }
            }


        }
    public static final class receive extends FlowLogic {

        @NotNull
        private final FlowSession otherSideSession;


        public receive(FlowSession otherSideSessions) {
            this.otherSideSession = otherSideSessions;

        }

        private final ProgressTracker.Step RECEIVING_IDENTITIES = new ProgressTracker.Step("Receiving confidential identities");
        private final ProgressTracker.Step RECEIVING_CERTIFICATES = new ProgressTracker.Step("Receiving certificates for unknown identities");

        private final ProgressTracker progressTracker = new ProgressTracker(RECEIVING_IDENTITIES, RECEIVING_CERTIFICATES);


        @Override
        public ProgressTracker getProgressTracker() {
            return this.progressTracker;
        }

        @Nullable
        @Suspendable
        public SignedTransaction call() throws FlowException {


            progressTracker.setCurrentStep(RECEIVING_IDENTITIES);
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
            progressTracker.setCurrentStep(RECEIVING_CERTIFICATES);

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


}


