package com.template.cordapp.exception;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.CordaRuntimeException;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0007"},
   d2 = {"Lcom/synechron/cordapp/obligation/exception/NotaryNotFoundException;", "Lnet/corda/core/CordaRuntimeException;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "cordapp-contracts-states"}
)
public final class NotaryNotFoundException extends CordaRuntimeException {
   @NotNull
   private final String message;

   @NotNull
   public String getMessage() {
      return this.message;
   }

   public NotaryNotFoundException(@NotNull String message) {
      Intrinsics.checkParameterIsNotNull(message, "message");
      super(message);
      this.message = message;
   }
}

