package com.synechron.cordapp.contract;

import java.util.Collection;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractsDSL;
import net.corda.core.contracts.Requirements;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u000e2\u00020\u0001:\u0002\r\u000eB\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u001e\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0002J\u001e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0002J\u001e\u0010\f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0002¨\u0006\u000f"},
   d2 = {"Lcom/synechron/cordapp/contract/AssetTransferContract;", "Lnet/corda/core/contracts/Contract;", "()V", "verify", "", "tx", "Lnet/corda/core/transactions/LedgerTransaction;", "verifyConfirmRequest", "signers", "", "Ljava/security/PublicKey;", "verifyCreateRequest", "verifySettleRequest", "Commands", "Companion", "cordapp-contracts-states"}
)
public final class AssetTransferContract implements Contract {
   @NotNull
   private static final String ASSET_TRANSFER_CONTRACT_ID;
   public static final AssetTransferContract.Companion Companion = new AssetTransferContract.Companion((DefaultConstructorMarker)null);

   public void verify(@NotNull LedgerTransaction tx) {
      Intrinsics.checkParameterIsNotNull(tx, "tx");
      Collection $receiver$iv = (Collection)tx.getCommands();
      CommandWithParties command = ContractsDSL.requireSingleCommand($receiver$iv, AssetTransferContract.Commands.class);
      Set setOfSigners = CollectionsKt.toSet((Iterable)command.getSigners());
      AssetTransferContract.Commands var4 = (AssetTransferContract.Commands)command.getValue();
      if (var4 instanceof AssetTransferContract.Commands.CreateRequest) {
         this.verifyCreateRequest(tx, setOfSigners);
      } else if (var4 instanceof AssetTransferContract.Commands.ConfirmRequest) {
         this.verifyConfirmRequest(tx, setOfSigners);
      } else {
         if (!(var4 instanceof AssetTransferContract.Commands.SettleRequest)) {
            throw (Throwable)(new IllegalArgumentException("Unrecognised command."));
         }

         this.verifySettleRequest(tx, setOfSigners);
      }

   }

   private final void verifyCreateRequest(LedgerTransaction tx, Set signers) {
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
         }
      }
   }

   private final void verifyConfirmRequest(LedgerTransaction tx, Set signers) {
      Requirements var3 = Requirements.INSTANCE;
   }

   private final void verifySettleRequest(LedgerTransaction tx, Set signers) {
      Requirements var3 = Requirements.INSTANCE;
   }

   static {
      String var10000 = AssetTransferContract.class.getName();
      if (var10000 == null) {
         Intrinsics.throwNpe();
      }

      ASSET_TRANSFER_CONTRACT_ID = var10000;
   }

   @NotNull
   public static final String getASSET_TRANSFER_CONTRACT_ID() {
      return Companion.getASSET_TRANSFER_CONTRACT_ID();
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001:\u0003\u0002\u0003\u0004¨\u0006\u0005"},
      d2 = {"Lcom/synechron/cordapp/contract/AssetTransferContract$Commands;", "Lnet/corda/core/contracts/CommandData;", "ConfirmRequest", "CreateRequest", "SettleRequest", "cordapp-contracts-states"}
   )
   public interface Commands extends CommandData {
      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005¢\u0006\u0002\u0010\u0003¨\u0006\u0004"},
         d2 = {"Lcom/synechron/cordapp/contract/AssetTransferContract$Commands$CreateRequest;", "Lnet/corda/core/contracts/TypeOnlyCommandData;", "Lcom/synechron/cordapp/contract/AssetTransferContract$Commands;", "()V", "cordapp-contracts-states"}
      )
      public static final class CreateRequest extends TypeOnlyCommandData implements AssetTransferContract.Commands {
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005¢\u0006\u0002\u0010\u0003¨\u0006\u0004"},
         d2 = {"Lcom/synechron/cordapp/contract/AssetTransferContract$Commands$ConfirmRequest;", "Lnet/corda/core/contracts/TypeOnlyCommandData;", "Lcom/synechron/cordapp/contract/AssetTransferContract$Commands;", "()V", "cordapp-contracts-states"}
      )
      public static final class ConfirmRequest extends TypeOnlyCommandData implements AssetTransferContract.Commands {
      }

      @Metadata(
         mv = {1, 1, 8},
         bv = {1, 0, 2},
         k = 1,
         d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005¢\u0006\u0002\u0010\u0003¨\u0006\u0004"},
         d2 = {"Lcom/synechron/cordapp/contract/AssetTransferContract$Commands$SettleRequest;", "Lnet/corda/core/contracts/TypeOnlyCommandData;", "Lcom/synechron/cordapp/contract/AssetTransferContract$Commands;", "()V", "cordapp-contracts-states"}
      )
      public static final class SettleRequest extends TypeOnlyCommandData implements AssetTransferContract.Commands {
      }
   }

   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u001c\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0005\u0010\u0002\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/synechron/cordapp/contract/AssetTransferContract$Companion;", "", "()V", "ASSET_TRANSFER_CONTRACT_ID", "", "ASSET_TRANSFER_CONTRACT_ID$annotations", "getASSET_TRANSFER_CONTRACT_ID", "()Ljava/lang/String;", "cordapp-contracts-states"}
   )
   public static final class Companion {
      /** @deprecated */
      // $FF: synthetic method
      @JvmStatic
      public static void ASSET_TRANSFER_CONTRACT_ID$annotations() {
      }

      @NotNull
      public final String getASSET_TRANSFER_CONTRACT_ID() {
         return AssetTransferContract.ASSET_TRANSFER_CONTRACT_ID;
      }

      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}

