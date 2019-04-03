package com.template.cordapp.common.flows;

public final class IdentitySyncFlow {

   public static IdentitySyncFlow INSTANCE;

   public IdentitySyncFlow() {
      this.INSTANCE = this;
   }

   static {
      new IdentitySyncFlow();
   }
}