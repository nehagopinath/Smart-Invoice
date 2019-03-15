package com.template.cordapp.state;

import com.template.cordapp.contract.AssetContract.Commands.Transfer;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.CommandAndState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.OwnableState;
import net.corda.core.crypto.NullKeys;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u001c\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\b\u0018\u00002\u00020\u00012\u00020\u0002B+\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\t\u0010\u0017\u001a\u00020\u0004HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0004HÆ\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J\t\u0010\u001a\u001a\u00020\nHÆ\u0003J7\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00042\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\b\b\u0002\u0010\t\u001a\u00020\nHÆ\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fHÖ\u0003J\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0016J\t\u0010$\u001a\u00020%HÖ\u0001J\u000e\u0010&\u001a\b\u0012\u0004\u0012\u00020#0'H\u0016J\t\u0010(\u001a\u00020\u0004HÖ\u0001J\u0010\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020\nH\u0016J\u0006\u0010,\u001a\u00020\u0000R\u0011\u0010\u0005\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0014\u0010\t\u001a\u00020\nX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\n0\u0012X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016¨\u0006-"},
   d2 = {"Lcom/synechron/cordapp/state/Asset;", "Lnet/corda/core/contracts/OwnableState;", "Lnet/corda/core/schemas/QueryableState;", "cusip", "", "assetName", "purchaseCost", "Lnet/corda/core/contracts/Amount;", "Ljava/util/Currency;", "owner", "Lnet/corda/core/identity/AbstractParty;", "(Ljava/lang/String;Ljava/lang/String;Lnet/corda/core/contracts/Amount;Lnet/corda/core/identity/AbstractParty;)V", "getAssetName", "()Ljava/lang/String;", "getCusip", "getOwner", "()Lnet/corda/core/identity/AbstractParty;", "participants", "", "getParticipants", "()Ljava/util/List;", "getPurchaseCost", "()Lnet/corda/core/contracts/Amount;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "", "generateMappedObject", "Lnet/corda/core/schemas/PersistentState;", "schema", "Lnet/corda/core/schemas/MappedSchema;", "hashCode", "", "supportedSchemas", "", "toString", "withNewOwner", "Lnet/corda/core/contracts/CommandAndState;", "newOwner", "withoutOwner", "cordapp-contracts-states"}
)
public final class Asset implements OwnableState, QueryableState {
   @NotNull
   private final List participants;
   @NotNull
   private final String cusip;
   @NotNull
   private final String assetName;
   @NotNull
   private final Amount purchaseCost;
   @NotNull
   private final AbstractParty owner;

   @NotNull
   public List getParticipants() {
      return this.participants;
   }

   @NotNull
   public final Asset withoutOwner() {
      return copy$default(this, (String)null, (String)null, (Amount)null, (AbstractParty)NullKeys.INSTANCE.getNULL_PARTY(), 7, (Object)null);
   }

   @NotNull
   public CommandAndState withNewOwner(@NotNull AbstractParty newOwner) {
      Intrinsics.checkParameterIsNotNull(newOwner, "newOwner");
      return new CommandAndState((CommandData)(new Transfer()), (OwnableState)copy$default(this, (String)null, (String)null, (Amount)null, newOwner, 7, (Object)null));
   }

   @NotNull
   public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
      Intrinsics.checkParameterIsNotNull(schema, "schema");
      if (schema instanceof AssetSchemaV1) {
         return (PersistentState)(new PersistentAsset(this.cusip, this.assetName, this.purchaseCost.toString(), this.getOwner(), CollectionsKt.toMutableSet((Iterable)this.getParticipants())));
      } else {
         throw (Throwable)(new IllegalArgumentException("Unrecognised schema " + schema));
      }
   }

   @NotNull
   public Iterable supportedSchemas() {
      return (Iterable)SetsKt.setOf(AssetSchemaV1.INSTANCE);
   }

   @NotNull
   public final String getCusip() {
      return this.cusip;
   }

   @NotNull
   public final String getAssetName() {
      return this.assetName;
   }

   @NotNull
   public final Amount getPurchaseCost() {
      return this.purchaseCost;
   }

   @NotNull
   public AbstractParty getOwner() {
      return this.owner;
   }

   public Asset(@NotNull String cusip, @NotNull String assetName, @NotNull Amount purchaseCost, @NotNull AbstractParty owner) {
      Intrinsics.checkParameterIsNotNull(cusip, "cusip");
      Intrinsics.checkParameterIsNotNull(assetName, "assetName");
      Intrinsics.checkParameterIsNotNull(purchaseCost, "purchaseCost");
      Intrinsics.checkParameterIsNotNull(owner, "owner");
      super();
      this.cusip = cusip;
      this.assetName = assetName;
      this.purchaseCost = purchaseCost;
      this.owner = owner;
      this.participants = CollectionsKt.listOf(this.getOwner());
   }

   @NotNull
   public final String component1() {
      return this.cusip;
   }

   @NotNull
   public final String component2() {
      return this.assetName;
   }

   @NotNull
   public final Amount component3() {
      return this.purchaseCost;
   }

   @NotNull
   public final AbstractParty component4() {
      return this.getOwner();
   }

   @NotNull
   public final Asset copy(@NotNull String cusip, @NotNull String assetName, @NotNull Amount purchaseCost, @NotNull AbstractParty owner) {
      Intrinsics.checkParameterIsNotNull(cusip, "cusip");
      Intrinsics.checkParameterIsNotNull(assetName, "assetName");
      Intrinsics.checkParameterIsNotNull(purchaseCost, "purchaseCost");
      Intrinsics.checkParameterIsNotNull(owner, "owner");
      return new Asset(cusip, assetName, purchaseCost, owner);
   }

   // $FF: synthetic method
   // $FF: bridge method
   @NotNull
   public static Asset copy$default(Asset var0, String var1, String var2, Amount var3, AbstractParty var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.cusip;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.assetName;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.purchaseCost;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.getOwner();
      }

      return var0.copy(var1, var2, var3, var4);
   }

   public String toString() {
      return "Asset(cusip=" + this.cusip + ", assetName=" + this.assetName + ", purchaseCost=" + this.purchaseCost + ", owner=" + this.getOwner() + ")";
   }

   public int hashCode() {
      String var10000 = this.cusip;
      int var1 = (var10000 != null ? var10000.hashCode() : 0) * 31;
      String var10001 = this.assetName;
      var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
      Amount var2 = this.purchaseCost;
      var1 = (var1 + (var2 != null ? var2.hashCode() : 0)) * 31;
      AbstractParty var3 = this.getOwner();
      return var1 + (var3 != null ? var3.hashCode() : 0);
   }

   public boolean equals(Object var1) {
      if (this != var1) {
         if (var1 instanceof Asset) {
            Asset var2 = (Asset)var1;
            if (Intrinsics.areEqual(this.cusip, var2.cusip) && Intrinsics.areEqual(this.assetName, var2.assetName) && Intrinsics.areEqual(this.purchaseCost, var2.purchaseCost) && Intrinsics.areEqual(this.getOwner(), var2.getOwner())) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }
}

