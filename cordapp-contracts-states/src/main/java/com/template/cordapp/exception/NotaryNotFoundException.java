package com.template.cordapp.exception;


import net.corda.core.CordaRuntimeException;


public class NotaryNotFoundException extends CordaRuntimeException {

   private final String message;

   public String getMessage() {
      return this.message;
   }

   public NotaryNotFoundException(String message)
   {
      super(message);
      this.message = message;
   }
}

