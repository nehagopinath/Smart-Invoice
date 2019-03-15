package com.synechron.cordapp.schema;

import kotlin.Metadata;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0003"},
   d2 = {"Lcom/synechron/cordapp/schema/AssetSchema;", "", "()V", "cordapp-contracts-states"}
)
public final class AssetSchema {
   public static final AssetSchema INSTANCE;

   private AssetSchema() {
      INSTANCE = (AssetSchema)this;
   }

   static {
      new AssetSchema();
   }
}

