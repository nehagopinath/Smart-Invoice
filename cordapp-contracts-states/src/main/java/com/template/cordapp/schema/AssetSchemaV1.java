package com.template.cordapp.schema;

import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import com.google.common.collect.ImmutableList;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AssetSchemaV1 extends MappedSchema {

   public AssetSchemaV1() {
       super(AssetSchema.class,1,ImmutableList.of(PersistentAsset.class));

   }

   @Entity
   @Table(
      name = "asset",
      indexes = {@Index(columnList = "owner", name = "idx_asset_owner"), @Index(columnList = "cusip", name = "idx_asset_cusip")}
   )

   public static class PersistentAsset extends PersistentState {

      @Column(name = "cusip") private final String cusip;
      @Column(name = "asset_name") private final String assetName;
      @Column(name = "purchase_cost") private final String purchaseCost;
      @Column(name = "owner") private final AbstractParty owner;


      @ElementCollection
      @Column(name = "participants")
      @CollectionTable(name = "asset_participants", joinColumns = {
              @JoinColumn(referencedColumnName = "output_index", name = "output_index"),
              @JoinColumn(referencedColumnName = "transaction_id", name = "transaction_id")
      }
      )
      @Nullable
      private Set participants;

      @NotNull
      public final String getCusip() {
         return this.cusip;
      }

      @NotNull
      public final String getAssetName() {
         return this.assetName;
      }

      @NotNull
      public final String getPurchaseCost() {
         return this.purchaseCost;
      }

      @NotNull
      public final AbstractParty getOwner() {
         return this.owner;
      }

      @Nullable
      public final Set getParticipants() {
          return this.participants;
      }

      public final void setParticipants(Set participants) {
         this.participants = participants;
      }

      public PersistentAsset(String cusip,String assetName,String purchaseCost,AbstractParty owner,Set participants) {
         this.cusip = cusip;
         this.assetName = assetName;
         this.purchaseCost = purchaseCost;
         this.owner = owner;
         this.participants = participants;
      }

       // Default constructor required by hibernate
       public PersistentAsset() {
           this.cusip = null;
           this.assetName = null;
           this.purchaseCost = null;
           this.owner = null;
           this.participants = null;
       }
   }
}

