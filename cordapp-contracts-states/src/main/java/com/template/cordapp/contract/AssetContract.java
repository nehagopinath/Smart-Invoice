package com.template.cordapp.contract;

import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import java.security.PublicKey;
import java.util.*;
import kotlin.collections.CollectionsKt;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.finance.contracts.asset.Cash;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class AssetContract implements Contract {

   public static final String ASSET_CONTRACT_ID = "com.template.cordapp.contract.AssetContract";

   public interface Commands extends CommandData {
      class Create implements Commands {
         @Override
         public boolean equals(Object obj) {
            return obj instanceof Create;
         }
      }

      class Transfer implements Commands {
         @Override
         public boolean equals(Object obj) {
            return obj instanceof Transfer;
         }
      }
   }

   public void verify(LedgerTransaction tx) {

      final CommandWithParties<Commands.Create> create = requireSingleCommand(tx.getCommands(), Commands.Create.class);
      final CommandWithParties<Commands.Transfer> transfer = requireSingleCommand(tx.getCommands(), Commands.Transfer.class);
      final List<PublicKey> requiredSigners_create = create.getSigners();
      final List<PublicKey> requiredSigners_transfer = transfer.getSigners();

      if (create.getValue() instanceof Commands.Create) {

         verifyCreate(tx, requiredSigners_create);

      } else if (transfer.getValue() instanceof Commands.Transfer) {
         verifyTransfer(tx, requiredSigners_transfer);
      } else
         try {
            throw new IllegalAccessException("Unrecognized Command");
         } catch (IllegalAccessException e) {
            e.printStackTrace();
         }

   }

   public void verifyCreate(LedgerTransaction tx, List signers) {

      final Asset output = tx.outputsOfType(Asset.class).get(0);

      if (!tx.getInputs().isEmpty())
         throw new IllegalArgumentException("No inputs should be consumed.");
      if (!(tx.getOutputs().size() == 1))
         throw new IllegalArgumentException("There should be one output state created.");
      //todo: add condition to check that the purchase cost should not be negative
      //if (output.getPurchaseCost().)
      //   throw new IllegalArgumentException("The purchase cost value must be non-negative.");
      if (!(signers.contains(output.getOwner().getOwningKey())))
         throw new IllegalArgumentException("Owner only may sign the Asset issue transaction.");

   }

   public void verifyTransfer(LedgerTransaction tx, List signers) {
      List input_assetTransfer = tx.inputsOfType(AssetTransfer.class);
      List input_asset = tx.inputsOfType(Asset.class);

      if (!(input_asset.size() == 1))
         throw new IllegalArgumentException("There must be one input obligation.");

      List cash = tx.outputsOfType(Cash.State.class);

      if ((cash.isEmpty()))
         throw new IllegalArgumentException("There must be output cash.");

      //todo: add condition to check that the recipient recives the cash
      //List inputAsset = input_asset.
      //val inputAssetTransfer = inputAssetTransfers.single()
      //val acceptableCash = cash.filter { it.owner in listOf(inputAsset.owner, inputAssetTransfer.securitySeller) }

      //todo: add condition to check that the amount settled is equal to the purchase cost of the asset.

      List outputs = tx.outputsOfType(Asset.class);

      if (!(outputs.size() == 1))
         throw new IllegalArgumentException("There must be one output asset");

      Asset output = (Asset) CollectionsKt.single(outputs);

      //todo: add condition to check -Must not not change Asset data except owner field value.

      //input_asset == output.copy(owner = input_asset.owner);

      if (!(signers.contains(output.getOwner().getOwningKey())))
         throw new IllegalArgumentException("Owner only may sign the Asset issue transaction.");

   }

}











