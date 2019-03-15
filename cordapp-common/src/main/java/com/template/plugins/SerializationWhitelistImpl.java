package com.synechron.cordapp.plugins;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import net.corda.core.serialization.SerializationWhitelist;
import net.corda.core.transactions.TransactionBuilder;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002R\u001e\u0010\u0003\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00050\u00048VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
   d2 = {"Lcom/synechron/cordapp/plugins/SerializationWhitelistImpl;", "Lnet/corda/core/serialization/SerializationWhitelist;", "()V", "whitelist", "", "Ljava/lang/Class;", "getWhitelist", "()Ljava/util/List;", "cordapp-common"}
)
public final class SerializationWhitelistImpl implements SerializationWhitelist {
   @NotNull
   public List getWhitelist() {
      return CollectionsKt.listOf(TransactionBuilder.class);
   }
}

