package com.template.cordapp.contract;

import java.security.PublicKey;
import java.util.List;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;


import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class AssetTransferContract implements Contract {

   public static final String ASSET_CONTRACT_ID = "AssetTransferContract";

   public interface Commands extends CommandData {
      class CreateRequest implements Commands {
         @Override
         public boolean equals(Object obj) {
            return obj instanceof CreateRequest;
         }
      }

      class ConfirmRequest implements Commands {
         @Override
         public boolean equals(Object obj) {
            return obj instanceof ConfirmRequest;
         }
      }

      class SettleRequest implements Commands {
         @Override
         public boolean equals(Object obj) {
            return obj instanceof SettleRequest;
         }
      }
   }

   public void verify(LedgerTransaction tx) {

      final CommandWithParties<AssetTransferContract.Commands.CreateRequest> createRequest = requireSingleCommand(tx.getCommands(), AssetTransferContract.Commands.CreateRequest.class);
      final CommandWithParties<AssetTransferContract.Commands.ConfirmRequest> confirmRequest = requireSingleCommand(tx.getCommands(), AssetTransferContract.Commands.ConfirmRequest.class);
      final CommandWithParties<AssetTransferContract.Commands.SettleRequest> settleRequest = requireSingleCommand(tx.getCommands(), AssetTransferContract.Commands.SettleRequest.class);

      final List<PublicKey> requiredSigners_createRequest = createRequest.getSigners();
      final List<PublicKey> requiredSigners_confirmRequest = confirmRequest.getSigners();
      final List<PublicKey> requiredSigners_settleRequest = settleRequest.getSigners();


      if (createRequest.getValue() instanceof AssetTransferContract.Commands.CreateRequest)
      {
         verifyCreateRequest(tx, requiredSigners_createRequest);
      }
      else if (confirmRequest.getValue() instanceof AssetTransferContract.Commands.ConfirmRequest)
      {
         verifyConfirmRequest(tx, requiredSigners_confirmRequest);
      }
      else if (settleRequest.getValue() instanceof AssetTransferContract.Commands.SettleRequest)
      {
         verifySettleRequest(tx, requiredSigners_settleRequest);
      }
      else
         try {
            throw new IllegalAccessException("Unrecognized Command");
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         }

   }

   public void verifyCreateRequest(LedgerTransaction tx, List signers)
   {

      if (!tx.getInputs().isEmpty())
         throw new IllegalArgumentException("No inputs should be consumed.");
      if (!(tx.getOutputs().size() == 1))
         throw new IllegalArgumentException("There should be one output state created.");

   }

   public void verifyConfirmRequest(LedgerTransaction tx, List signers)
   {
     //todo: Add rules
   }

   public void verifySettleRequest(LedgerTransaction tx, List signers)
   {
      //todo: Add Rules
   }

}

