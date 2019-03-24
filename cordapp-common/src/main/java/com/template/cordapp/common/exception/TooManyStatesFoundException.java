package com.template.cordapp.common.exception;

import net.corda.core.CordaRuntimeException;
import org.jetbrains.annotations.NotNull;


public final class TooManyStatesFoundException extends CordaRuntimeException {

   private final String message;


   public String getMessage() {
      return this.message;
   }

   public TooManyStatesFoundException(String message) {
      super(message);
      this.message = message;
   }
}

