package com.template.cordapp.flows;

import com.template.cordapp.exception.NotaryNotFoundException;
import com.template.cordapp.exception.StateNotFoundOnVaultException;

import java.util.List;
import java.util.Set;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.Vault.StateStatus;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.node.services.vault.QueryCriteria.LinearStateQueryCriteria;
import org.jetbrains.annotations.NotNull;

public interface FlowLogicCommonMethods {
   Party firstNotary(@NotNull ServiceHub $receiver);
   StateAndRef loadState(@NotNull ServiceHub $receiver, @NotNull UniqueIdentifier linearId, @NotNull Class clazz);
   Party resolveIdentity(@NotNull ServiceHub $receiver, @NotNull AbstractParty abstractParty);


   public static final class DefaultImpls {
      @NotNull
      public static Party firstNotary(@NotNull ServiceHub receiver) {
         Intrinsics.checkParameterIsNotNull(receiver, "receiver");
         Party var10000 = (Party)CollectionsKt.firstOrNull(receiver.getNetworkMapCache().getNotaryIdentities());
         if (var10000 != null) {
            return var10000;
         } else {
            throw (new NotaryNotFoundException("No available notary."));
         }
      }

      @NotNull
      public static StateAndRef loadState(@NotNull ServiceHub receiver,
                                          @NotNull UniqueIdentifier linearId,
                                          @NotNull  Class cls) {
         Intrinsics.checkParameterIsNotNull(receiver, "receiver");
         Intrinsics.checkParameterIsNotNull(linearId, "linearId");
         Intrinsics.checkParameterIsNotNull(cls, "cls");
         LinearStateQueryCriteria queryCriteria = new LinearStateQueryCriteria((List)null, CollectionsKt.listOf(linearId), StateStatus.UNCONSUMED, (Set)null);
         StateAndRef var10000 = (StateAndRef)CollectionsKt.singleOrNull(receiver.getVaultService().queryBy(cls, (QueryCriteria)queryCriteria).getStates());
         if (var10000 != null) {
            return var10000;
         } else {
            throw (new StateNotFoundOnVaultException("State with id " + linearId + " not found."));
         }
      }

      @NotNull
      public static Party resolveIdentity(@NotNull ServiceHub receiver, @NotNull AbstractParty abstractParty) {
         Intrinsics.checkParameterIsNotNull(receiver, "$receiver");
         Intrinsics.checkParameterIsNotNull(abstractParty, "abstractParty");
         return receiver.getIdentityService().requireWellKnownPartyFromAnonymous(abstractParty);
      }
   }
}

