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
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.PartyAndCertificate;
import net.corda.core.transactions.WireTransaction;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.UntrustworthyData;
import net.corda.core.utilities.ProgressTracker.Step;


public final class IdentitySyncFlow {
   public static final IdentitySyncFlow INSTANCE;

   private IdentitySyncFlow() {
      INSTANCE = this;
   }

   static {
      new IdentitySyncFlow();
   }


   public static final class Send extends FlowLogic {

      private final Set otherSideSessions;

      private final WireTransaction tx;

      private final ProgressTracker progressTracker;


      public static final IdentitySyncFlow.Send.Companion Companion = new IdentitySyncFlow.Send.Companion((DefaultConstructorMarker)null);

      @Suspendable
      public void call() {
         this.getProgressTracker().setCurrentStep((Step)IdentitySyncFlow.Send.Companion.SYNCING_IDENTITIES.INSTANCE);
         Iterable $receiver$iv = (Iterable)this.tx.getInputs();
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
         Iterator var5 = $receiver$iv.iterator();

         Object item$iv$iv;
         while(var5.hasNext()) {
            item$iv$iv = var5.next();
            StateRef it = (StateRef)item$iv$iv;
            TransactionState var26 = this.getServiceHub().loadState(it);
            destination$iv$iv.add(var26);
         }

         $receiver$iv = (Iterable)CollectionsKt.requireNoNulls((List)destination$iv$iv);
         destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
         var5 = $receiver$iv.iterator();

         TransactionState it;
         while(var5.hasNext()) {
            item$iv$iv = var5.next();
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
                  throw (Throwable)(new IllegalStateException("Counterparty requested a confidential identity for which we do not have the certificate path: " + this.tx.getId()));
               }

               destination$iv$iv.add(identityCertificate);
            }

            List sendIdentities = (List)destination$iv$iv;
            otherSideSession.send(sendIdentities);
         }

      }

      @NotNull
      public final Set getOtherSideSessions() {
         return this.otherSideSessions;
      }

      @NotNull
      public final WireTransaction getTx() {
         return this.tx;
      }

      @NotNull
      public ProgressTracker getProgressTracker() {
         return this.progressTracker;
      }

      public Send(@NotNull Set otherSideSessions, @NotNull WireTransaction tx, @NotNull ProgressTracker progressTracker) {
         Intrinsics.checkParameterIsNotNull(otherSideSessions, "otherSideSessions");
         Intrinsics.checkParameterIsNotNull(tx, "tx");
         Intrinsics.checkParameterIsNotNull(progressTracker, "progressTracker");
         super();
         this.otherSideSessions = otherSideSessions;
         this.tx = tx;
         this.progressTracker = progressTracker;
      }

      public Send(@NotNull FlowSession otherSide, @NotNull WireTransaction tx) {
         Intrinsics.checkParameterIsNotNull(otherSide, "otherSide");
         Intrinsics.checkParameterIsNotNull(tx, "tx");
         this(SetsKt.setOf(otherSide), tx, Companion.tracker());
      }


      public static final class Companion {
         @NotNull
         public final ProgressTracker tracker() {
            return new ProgressTracker(new Step[]{(Step)IdentitySyncFlow.Send.Companion.SYNCING_IDENTITIES.INSTANCE});
         }

         private Companion() {
         }

         // $FF: synthetic method
         public Companion() {
            this();
         }


         public static final class SYNCING_IDENTITIES extends Step {
            public static final IdentitySyncFlow.Send.Companion.SYNCING_IDENTITIES INSTANCE;

            private SYNCING_IDENTITIES() {
               super("Syncing identities");
               INSTANCE = (IdentitySyncFlow.Send.Companion.SYNCING_IDENTITIES)this;
            }

            static {
               new IdentitySyncFlow.Send.Companion.SYNCING_IDENTITIES();
            }
         }
      }
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \r2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\rB\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J\b\u0010\f\u001a\u00020\u0002H\u0017R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u000e"},
      d2 = {"Lcom/synechron/cordapp/common/flows/IdentitySyncFlow$Receive;", "Lnet/corda/core/flows/FlowLogic;", "", "otherSideSession", "Lnet/corda/core/flows/FlowSession;", "(Lnet/corda/core/flows/FlowSession;)V", "getOtherSideSession", "()Lnet/corda/core/flows/FlowSession;", "progressTracker", "Lnet/corda/core/utilities/ProgressTracker;", "getProgressTracker", "()Lnet/corda/core/utilities/ProgressTracker;", "call", "Companion", "cordapp-common"}
   )
   public static final class Receive extends FlowLogic {
      @NotNull
      private final ProgressTracker progressTracker;
      @NotNull
      private final FlowSession otherSideSession;
      public static final IdentitySyncFlow.Receive.Companion Companion = new IdentitySyncFlow.Receive.Companion((DefaultConstructorMarker)null);

      @NotNull
      public ProgressTracker getProgressTracker() {
         return this.progressTracker;
      }

      @Suspendable
      public void call() {
         this.getProgressTracker().setCurrentStep((Step)IdentitySyncFlow.Receive.Companion.RECEIVING_IDENTITIES.INSTANCE);
         FlowSession this_$iv = this.otherSideSession;
         UntrustworthyData $receiver$iv = this_$iv.receive(List.class);
         List it = (List)$receiver$iv.getFromUntrustedWorld();
         Iterable $receiver$iv = (Iterable)it;
         Collection destination$iv$iv = (Collection)(new ArrayList());
         Iterator var6 = $receiver$iv.iterator();

         while(var6.hasNext()) {
            Object element$iv$iv = var6.next();
            AbstractParty it = (AbstractParty)element$iv$iv;
            if (this.getServiceHub().getIdentityService().wellKnownPartyFromAnonymous(it) == null) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         List unknownIdentities = (List)destination$iv$iv;
         this.getProgressTracker().setCurrentStep((Step)IdentitySyncFlow.Receive.Companion.RECEIVING_CERTIFICATES.INSTANCE);
         FlowSession this_$iv = this.otherSideSession;
         UntrustworthyData missingIdentities = this_$iv.sendAndReceive(List.class, unknownIdentities);
         List identities = (List)missingIdentities.getFromUntrustedWorld();
         Iterable $receiver$iv = (Iterable)identities;
         Iterator var23 = $receiver$iv.iterator();

         while(var23.hasNext()) {
            Object element$iv = var23.next();
            PartyAndCertificate it = (PartyAndCertificate)element$iv;
            it.verify(this.getServiceHub().getIdentityService().getTrustAnchor());
         }

         Iterable $receiver$iv = (Iterable)identities;
         Iterator var20 = $receiver$iv.iterator();

         while(var20.hasNext()) {
            Object element$iv = var20.next();
            PartyAndCertificate identity = (PartyAndCertificate)element$iv;
            this.getServiceHub().getIdentityService().verifyAndRegisterIdentity(identity);
         }

      }

      // $FF: synthetic method
      // $FF: bridge method
      public Object call() {
         this.call();
         return Unit.INSTANCE;
      }

      @NotNull
      public final FlowSession getOtherSideSession() {
         return this.otherSideSession;
      }

      public Receive(@NotNull FlowSession otherSideSession) {
         Intrinsics.checkParameterIsNotNull(otherSideSession, "otherSideSession");
         super();
         this.otherSideSession = otherSideSession;
         this.progressTracker = new ProgressTracker(new Step[]{(Step)IdentitySyncFlow.Receive.Companion.RECEIVING_IDENTITIES.INSTANCE, (Step)IdentitySyncFlow.Receive.Companion.RECEIVING_CERTIFICATES.INSTANCE});
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0002\u0003\u0004B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0005"},
         d2 = {"Lcom/synechron/cordapp/common/flows/IdentitySyncFlow$Receive$Companion;", "", "()V", "RECEIVING_CERTIFICATES", "RECEIVING_IDENTITIES", "cordapp-common"}
      )
      public static final class Companion {
         private Companion() {
         }

         // $FF: synthetic method
         public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
         }

         @Metadata(
            mv = {1, 1, 8},
            bv = {1, 0, 2},
            k = 1,
            d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
            d2 = {"Lcom/synechron/cordapp/common/flows/IdentitySyncFlow$Receive$Companion$RECEIVING_IDENTITIES;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-common"}
         )
         public static final class RECEIVING_IDENTITIES extends Step {
            public static final IdentitySyncFlow.Receive.Companion.RECEIVING_IDENTITIES INSTANCE;

            private RECEIVING_IDENTITIES() {
               super("Receiving confidential identities");
               INSTANCE = (IdentitySyncFlow.Receive.Companion.RECEIVING_IDENTITIES)this;
            }

            static {
               new IdentitySyncFlow.Receive.Companion.RECEIVING_IDENTITIES();
            }
         }

         @Metadata(
            mv = {1, 1, 8},
            bv = {1, 0, 2},
            k = 1,
            d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
            d2 = {"Lcom/synechron/cordapp/common/flows/IdentitySyncFlow$Receive$Companion$RECEIVING_CERTIFICATES;", "Lnet/corda/core/utilities/ProgressTracker$Step;", "()V", "cordapp-common"}
         )
         public static final class RECEIVING_CERTIFICATES extends Step {
            public static final IdentitySyncFlow.Receive.Companion.RECEIVING_CERTIFICATES INSTANCE;

            private RECEIVING_CERTIFICATES() {
               super("Receiving certificates for unknown identities");
               INSTANCE = (IdentitySyncFlow.Receive.Companion.RECEIVING_CERTIFICATES)this;
            }

            static {
               new IdentitySyncFlow.Receive.Companion.RECEIVING_CERTIFICATES();
            }
         }
      }
   }
}

