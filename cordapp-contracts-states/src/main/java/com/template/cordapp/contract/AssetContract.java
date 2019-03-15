package com.template.cordapp.contract;

import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractsDSL;
import net.corda.core.contracts.Requirements;
import net.corda.core.contracts.Structures;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.finance.contracts.asset.Cash.State;
import net.corda.finance.utils.StateSumming;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 \r2\u00020\u0001:\u0002\f\rB\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u001e\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0002J\u001e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0002¨\u0006\u000e"},
   d2 = {"Lcom/synechron/cordapp/contract/AssetContract;", "Lnet/corda/core/contracts/Contract;", "()V", "verify", "", "tx", "Lnet/corda/core/transactions/LedgerTransaction;", "verifyCreate", "signers", "", "Ljava/security/PublicKey;", "verifyTransfer", "Commands", "Companion", "cordapp-contracts-states"}
)
public final class AssetContract implements Contract {
   @NotNull
   private static final String ASSET_CONTRACT_ID;
   public static final AssetContract.Companion Companion = new AssetContract.Companion((DefaultConstructorMarker)null);

   public void verify(@NotNull LedgerTransaction tx) {
      Intrinsics.checkParameterIsNotNull(tx, "tx");
      Collection $receiver$iv = (Collection)tx.getCommands();
      CommandWithParties command = ContractsDSL.requireSingleCommand($receiver$iv, AssetContract.Commands.class);
      Set setOfSigners = CollectionsKt.toSet((Iterable)command.getSigners());
      AssetContract.Commands var4 = (AssetContract.Commands)command.getValue();
      if (var4 instanceof AssetContract.Commands.Create) {
         this.verifyCreate(tx, setOfSigners);
      } else {
         if (!(var4 instanceof AssetContract.Commands.Transfer)) {
            throw (Throwable)(new IllegalArgumentException("Unrecognised command."));
         }

         this.verifyTransfer(tx, setOfSigners);
      }

   }

   private final void verifyCreate(LedgerTransaction tx, Set signers) {
      Requirements $receiver = Requirements.INSTANCE;
      String $receiver$iv = "No inputs must be consumed.";
      boolean expr$iv = tx.getInputStates().isEmpty();
      if (!expr$iv) {
         throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
      } else {
         $receiver$iv = "Only one out state should be created.";
         expr$iv = tx.getOutputStates().size() == 1;
         if (!expr$iv) {
            throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
         } else {
            Asset output = (Asset)CollectionsKt.single(tx.outputsOfType(Asset.class));
            String $receiver$iv = "Must have a positive amount.";
            boolean expr$iv = output.getPurchaseCost().compareTo(Amount.copy$default(output.getPurchaseCost(), 0L, (BigDecimal)null, (Object)null, 6, (Object)null)) > 0;
            if (!expr$iv) {
               throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
            } else {
               $receiver$iv = "Owner only may sign the Asset issue transaction.";
               expr$iv = signers.contains(output.getOwner().getOwningKey());
               if (!expr$iv) {
                  throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
               }
            }
         }
      }
   }

   private final void verifyTransfer(LedgerTransaction tx, Set signers) {
      Requirements $receiver = Requirements.INSTANCE;
      List inputAssets = tx.inputsOfType(Asset.class);
      List inputAssetTransfers = tx.inputsOfType(AssetTransfer.class);
      String $receiver$iv = "There must be one input obligation.";
      boolean expr$iv = inputAssets.size() == 1;
      if (!expr$iv) {
         throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
      } else {
         List cash = tx.outputsOfType(State.class);
         String $receiver$iv = "There must be output cash.";
         Collection var9 = (Collection)cash;
         boolean expr$iv = !var9.isEmpty();
         if (!expr$iv) {
            throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
         } else {
            Asset inputAsset = (Asset)CollectionsKt.single(inputAssets);
            AssetTransfer inputAssetTransfer = (AssetTransfer)CollectionsKt.single(inputAssetTransfers);
            Iterable $receiver$iv = (Iterable)cash;
            Collection destination$iv$iv = (Collection)(new ArrayList());
            Iterator var13 = $receiver$iv.iterator();

            while(var13.hasNext()) {
               Object element$iv$iv = var13.next();
               State it = (State)element$iv$iv;
               if (CollectionsKt.listOf(new AbstractParty[]{inputAsset.getOwner(), inputAssetTransfer.getSecuritySeller()}).contains(it.getOwner())) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            List acceptableCash = (List)destination$iv$iv;
            String $receiver$iv = "There must be output cash paid to the recipient.";
            destination$iv$iv = (Collection)acceptableCash;
            boolean expr$iv = !destination$iv$iv.isEmpty();
            if (!expr$iv) {
               throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
            } else {
               Amount sumAcceptableCash = Structures.withoutIssuer(StateSumming.sumCash((Iterable)acceptableCash));
               String $receiver$iv = "The amount settled must be equal to the asset's purchase cost amount.";
               boolean expr$iv = Intrinsics.areEqual(inputAsset.getPurchaseCost(), sumAcceptableCash);
               if (!expr$iv) {
                  throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
               } else {
                  List outputs = tx.outputsOfType(Asset.class);
                  String $receiver$iv = "There must be one output Asset.";
                  boolean expr$iv = outputs.size() == 1;
                  if (!expr$iv) {
                     throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
                  } else {
                     Asset output = (Asset)CollectionsKt.single(outputs);
                     String $receiver$iv = "Must not not change Asset data except owner field value.";
                     boolean expr$iv = Intrinsics.areEqual(inputAsset, Asset.copy$default(output, (String)null, (String)null, (Amount)null, inputAsset.getOwner(), 7, (Object)null));
                     if (!expr$iv) {
                        throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
                     } else {
                        $receiver$iv = "Owner only may sign the Asset issue transaction.";
                        expr$iv = signers.contains(output.getOwner().getOwningKey());
                        if (!expr$iv) {
                           throw (Throwable)(new IllegalArgumentException("Failed requirement: " + $receiver$iv));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   static {
      String var10000 = AssetContract.class.getName();
      if (var10000 == null) {
         Intrinsics.throwNpe();
      }

      ASSET_CONTRACT_ID = var10000;
   }

   @NotNull
   public static final String getASSET_CONTRACT_ID() {
      return Companion.getASSET_CONTRACT_ID();
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001:\u0002\u0002\u0003¨\u0006\u0004"},
      d2 = {"Lcom/synechron/cordapp/contract/AssetContract$Commands;", "Lnet/corda/core/contracts/CommandData;", "Create", "Transfer", "cordapp-contracts-states"}
   )
   public interface Commands extends CommandData {
      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005¢\u0006\u0002\u0010\u0003¨\u0006\u0004"},
         d2 = {"Lcom/synechron/cordapp/contract/AssetContract$Commands$Create;", "Lnet/corda/core/contracts/TypeOnlyCommandData;", "Lcom/synechron/cordapp/contract/AssetContract$Commands;", "()V", "cordapp-contracts-states"}
      )
      public static final class Create extends TypeOnlyCommandData implements AssetContract.Commands {
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005¢\u0006\u0002\u0010\u0003¨\u0006\u0004"},
         d2 = {"Lcom/synechron/cordapp/contract/AssetContract$Commands$Transfer;", "Lnet/corda/core/contracts/TypeOnlyCommandData;", "Lcom/synechron/cordapp/contract/AssetContract$Commands;", "()V", "cordapp-contracts-states"}
      )
      public static final class Transfer extends TypeOnlyCommandData implements AssetContract.Commands {
      }
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001c\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0005\u0010\u0002\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/synechron/cordapp/contract/AssetContract$Companion;", "", "()V", "ASSET_CONTRACT_ID", "", "ASSET_CONTRACT_ID$annotations", "getASSET_CONTRACT_ID", "()Ljava/lang/String;", "cordapp-contracts-states"}
   )
   public static final class Companion {
      /** @deprecated */
      // $FF: synthetic method
      @JvmStatic
      public static void ASSET_CONTRACT_ID$annotations() {
      }

      @NotNull
      public final String getASSET_CONTRACT_ID() {
         return AssetContract.ASSET_CONTRACT_ID;
      }

      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}

