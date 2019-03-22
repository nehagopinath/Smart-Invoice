package com.template.cordapp.schema;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.google.common.collect.ImmutableList;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.crypto.NullKeys;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.PersistentStateRef;
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
       // TODO check according to the example schema the following should be "private final"
      @Column(name = "cusip") private String cusip;
      @Column(name = "asset_name") private  String assetName;
      @Column(name = "purchase_cost") private  String purchaseCost;
      @Column(name = "owner") private AbstractParty owner;


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

      //TODO check : setter is not required here i guess
      /*
      public final void setParticipants(@Nullable Set <set-?>) {
         this.participants = var1;
      }*/

      public PersistentAsset(@NotNull String cusip, @NotNull String assetName, @NotNull String purchaseCost, @NotNull AbstractParty owner, @Nullable Set participants) {
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

