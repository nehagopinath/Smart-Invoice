package com.template.cordapp.flows;

import com.template.cordapp.exception.NotaryNotFoundException;
import com.template.cordapp.exception.StateNotFoundOnVaultException;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
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

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\f\u0010\u0002\u001a\u00020\u0003*\u00020\u0004H\u0016J2\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00070\u0006\"\b\b\u0000\u0010\u0007*\u00020\b*\u00020\u00042\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\u00070\fH\u0016J\u0014\u0010\r\u001a\u00020\u0003*\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000fH\u0016¨\u0006\u0010"},
   d2 = {"Lcom/synechron/cordapp/flows/FlowLogicCommonMethods;", "", "firstNotary", "Lnet/corda/core/identity/Party;", "Lnet/corda/core/node/ServiceHub;", "loadState", "Lnet/corda/core/contracts/StateAndRef;", "T", "Lnet/corda/core/contracts/ContractState;", "linearId", "Lnet/corda/core/contracts/UniqueIdentifier;", "clazz", "Ljava/lang/Class;", "resolveIdentity", "abstractParty", "Lnet/corda/core/identity/AbstractParty;", "cordapp-contracts-states"}
)
public interface FlowLogicCommonMethods {
   @NotNull
   Party firstNotary(@NotNull ServiceHub $receiver);

   @NotNull
   StateAndRef loadState(@NotNull ServiceHub $receiver, @NotNull UniqueIdentifier linearId, @NotNull Class clazz);

   @NotNull
   Party resolveIdentity(@NotNull ServiceHub $receiver, @NotNull AbstractParty abstractParty);

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 3
   )
   public static final class DefaultImpls {
      @NotNull
      public static Party firstNotary(@NotNull FlowLogicCommonMethods $receiver, ServiceHub $receiver) {
         Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
         Party var10000 = (Party)CollectionsKt.firstOrNull($receiver.getNetworkMapCache().getNotaryIdentities());
         if (var10000 != null) {
            return var10000;
         } else {
            throw (Throwable)(new NotaryNotFoundException("No available notary."));
         }
      }

      @NotNull
      public static StateAndRef loadState(@NotNull FlowLogicCommonMethods $receiver, @NotNull ServiceHub linearId, @NotNull UniqueIdentifier clazz, Class clazz) {
         Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
         Intrinsics.checkParameterIsNotNull(linearId, "linearId");
         Intrinsics.checkParameterIsNotNull(clazz, "clazz");
         LinearStateQueryCriteria queryCriteria = new LinearStateQueryCriteria((List)null, CollectionsKt.listOf(linearId), StateStatus.UNCONSUMED, (Set)null);
         StateAndRef var10000 = (StateAndRef)CollectionsKt.singleOrNull($receiver.getVaultService().queryBy(clazz, (QueryCriteria)queryCriteria).getStates());
         if (var10000 != null) {
            return var10000;
         } else {
            throw (Throwable)(new StateNotFoundOnVaultException("State with id " + linearId + " not found."));
         }
      }

      @NotNull
      public static Party resolveIdentity(@NotNull FlowLogicCommonMethods $receiver, @NotNull ServiceHub abstractParty, AbstractParty abstractParty) {
         Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
         Intrinsics.checkParameterIsNotNull(abstractParty, "abstractParty");
         return $receiver.getIdentityService().requireWellKnownPartyFromAnonymous(abstractParty);
      }
   }
}

