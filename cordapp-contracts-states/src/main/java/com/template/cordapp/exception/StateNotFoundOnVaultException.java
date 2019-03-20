package com.template.cordapp.exception;

import net.corda.core.CordaRuntimeException;


public final class StateNotFoundOnVaultException extends CordaRuntimeException
{

   private final String message;

   public String getMessage()
   {
      return this.message;
   }

   public StateNotFoundOnVaultException(String message)
   {
      super(message);
      this.message = message;
   }
}

