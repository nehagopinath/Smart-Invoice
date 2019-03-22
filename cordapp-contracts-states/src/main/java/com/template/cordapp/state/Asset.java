package com.template.cordapp.state;

import com.template.cordapp.contract.AssetContract;
import com.template.cordapp.contract.AssetContract.Commands.Transfer;

import java.util.List;
import java.util.Objects;

import com.template.cordapp.contract.AssetContract.Commands;
import com.template.cordapp.schema.AssetSchema;
import com.template.cordapp.schema.AssetSchemaV1;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.*;
import net.corda.core.crypto.NullKeys;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;
import com.google.common.collect.ImmutableList;


/**
 * This states plays role of digital asset (i.e. bond, securities, stock, etc.) on ledger.
 */

public final class Asset implements OwnableState, QueryableState {

   private final String cusip;
   private final String assetName;
   //we can include type of currency when we decide to use one, for now this is units
   private final Amount purchaseCost;
   private final AbstractParty owner;

   // TODO check : this was required (autofix by IDE) for the supportedSchema method
    private AssetSchema AssetSchemaV1;

    // TODO In the following method actually listOf() method was used in ref kotlin code, which I was unable to use in java
   @Override
   public List<AbstractParty> getParticipants() {
      return ImmutableList.of(owner);
   }

   // TODO  : not sure if this is really needed(unused method), if yes then is this syntactically right or wrong
   public final Asset withoutOwner() {
      return copy$default(this,
              (String)null,
              (String)null,
              (Amount)null,
              (AbstractParty)NullKeys.INSTANCE.getNULL_PARTY(),
              7,
              (Object)null);
   }

   //TODO : check OwnableState
   @Override
   public CommandAndState withNewOwner(@NotNull AbstractParty newOwner) {
      return new CommandAndState(
              (CommandData) new Transfer(),
              (OwnableState)copy$default(this, (String)null, (String)null, (Amount)null, newOwner, 7, (Object)null)
      );
   }

   public PersistentState generateMappedObject(@NotNull MappedSchema schema) throws IllegalArgumentException{
      if (schema instanceof AssetSchemaV1)
      {
         return (PersistentState)(new AssetSchemaV1.PersistentAsset(
                 this.cusip,
                 this.assetName,
                 this.purchaseCost.toString(),
                 this.getOwner(),
                 CollectionsKt.toMutableSet((Iterable)this.getParticipants())));
      } else {
         throw (new IllegalArgumentException("Unrecognised schema " + schema));
      }
   }


   public Iterable supportedSchemas() {
      return (Iterable)SetsKt.setOf(AssetSchemaV1);
   }


   public final String getCusip() {
      return this.cusip;
   }

   public final String getAssetName() {
      return this.assetName;
   }


   public final Amount getPurchaseCost() {
      return this.purchaseCost;
   }


   public AbstractParty getOwner() {
      return this.owner;
   }

   public Asset(@NotNull String cusip, @NotNull String assetName, @NotNull Amount purchaseCost, @NotNull AbstractParty owner) {
      super();
      this.cusip = cusip;
      this.assetName = assetName;
      this.purchaseCost = purchaseCost;
      this.owner = owner;
   }

   @NotNull
   public final Asset copy(@NotNull String cusip, @NotNull String assetName, @NotNull Amount purchaseCost, @NotNull AbstractParty owner) {
      return new Asset(cusip, assetName, purchaseCost, owner);
   }

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

   public boolean equals(Object o) {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;

       Asset that = (Asset)o;

       return (this.cusip.equals(that.cusip) && this.assetName.equals(that.assetName) && this.purchaseCost.equals(that.purchaseCost) && this.getOwner().equals(that.getOwner()));
   }
}

