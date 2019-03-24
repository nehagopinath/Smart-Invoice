package com.template.cordapp.plugins;

import java.util.List;
import kotlin.collections.CollectionsKt;
import net.corda.core.serialization.SerializationWhitelist;
import net.corda.core.transactions.TransactionBuilder;



public final class SerializationWhitelistImpl implements SerializationWhitelist {

   public List getWhitelist() {
      return CollectionsKt.listOf(TransactionBuilder.class);
   }
}

