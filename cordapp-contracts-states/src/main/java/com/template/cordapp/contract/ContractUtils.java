package com.synechron.cordapp.contract;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 2,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004¨\u0006\u0005"},
   d2 = {"keysFromParticipants", "", "Ljava/security/PublicKey;", "state", "Lnet/corda/core/contracts/ContractState;", "cordapp-contracts-states"}
)
public final class ContractUtils {
   @NotNull
   public static final Set keysFromParticipants(@NotNull ContractState state) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Iterable $receiver$iv = (Iterable)state.getParticipants();
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
      Iterator var4 = $receiver$iv.iterator();

      while(var4.hasNext()) {
         Object item$iv$iv = var4.next();
         AbstractParty it = (AbstractParty)item$iv$iv;
         PublicKey var11 = it.getOwningKey();
         destination$iv$iv.add(var11);
      }

      return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
   }
}

