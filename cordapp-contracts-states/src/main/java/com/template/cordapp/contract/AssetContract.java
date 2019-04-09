package com.template.cordapp.contract;

import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import java.security.PublicKey;
import java.util.*;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.finance.contracts.asset.Cash;
import net.corda.finance.utils.StateSumming;
import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

public class AssetContract implements Contract {

    public static final String ASSET_CONTRACT_ID = AssetContract.class.getName();

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
        if (output.getPurchaseCost().getQuantity() < 0)
            throw new IllegalArgumentException("The purchase cost value must be non-negative.");
        if (!(signers.contains(output.getOwner().getOwningKey())))
            throw new IllegalArgumentException("Owner only may sign the Asset issue transaction.");

    }

    public void verifyTransfer(LedgerTransaction tx, List signers) {
        List inputAssetTransfers = tx.inputsOfType(AssetTransfer.class);
        List inputAssets = tx.inputsOfType(Asset.class);

        if (!(inputAssets.size() == 1))
            throw new IllegalArgumentException("There must be one input obligation.");

        List cash = tx.outputsOfType(Cash.State.class);

        if ((cash.isEmpty()))
            throw new IllegalArgumentException("There must be output cash.");

        // Condition to check if recipient recieved cash

        Asset inputAsset = (Asset)CollectionsKt.single(inputAssets);
        AssetTransfer inputAssetTransfer = (AssetTransfer)CollectionsKt.single(inputAssetTransfers);
        Iterable $receiver$iv = (Iterable)cash;
        Collection destination$iv$iv = (Collection)(new ArrayList());
        Iterator var13 = $receiver$iv.iterator();

        while(var13.hasNext()) {
            Object element$iv$iv = var13.next();
            Cash.State it = (Cash.State)element$iv$iv;
            if (CollectionsKt.listOf(new AbstractParty[]{inputAsset.getOwner(), inputAssetTransfer.getSecuritySeller()}).contains(it.getOwner())) {
                destination$iv$iv.add(element$iv$iv);
            }
        }

        List acceptableCash = (List)destination$iv$iv;
        destination$iv$iv = (Collection)acceptableCash;

        if (destination$iv$iv.isEmpty()){
            throw new IllegalArgumentException("There must output cash paid to the recipient.");
        }

        // condition to check that the amount settled is equal to the purchase cost of the asset.

        Amount sumAcceptableCash = Structures.withoutIssuer(StateSumming.sumCash((Iterable)acceptableCash));
        if (!(Intrinsics.areEqual(inputAsset.getPurchaseCost(), sumAcceptableCash))){
            throw new IllegalArgumentException("Amount settled is not equal tp purchase cost.");
        }


        List outputs = tx.outputsOfType(Asset.class);

        if (!(outputs.size() == 1))
            throw new IllegalArgumentException("There must be one output asset");

        // add condition to check -Must not not change Asset data except owner field value.
        Asset output = (Asset) CollectionsKt.single(outputs);
        if(!(Intrinsics.areEqual(inputAsset, Asset.copy$default(output, (String)null, (String)null, (Amount)null, inputAsset.getOwner(), 7, (Object)null)))) {
            throw new IllegalArgumentException("Must not not change Asset data except owner field value");
        }

        //input_asset == output.copy(owner = input_asset.owner);

        if (!(signers.contains(output.getOwner().getOwningKey())))
            throw new IllegalArgumentException("Owner only may sign the Asset issue transaction.");

    }

}











