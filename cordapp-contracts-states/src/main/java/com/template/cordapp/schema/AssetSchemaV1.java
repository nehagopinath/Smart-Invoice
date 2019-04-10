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

    public static final AssetSchemaV1 INSTANCE;

    public AssetSchemaV1() {
       super(AssetSchema.INSTANCE.getClass(),1,CollectionsKt.listOf(AssetSchemaV1.PersistentAsset.class));

   }

    static {
        AssetSchemaV1 var0 = new AssetSchemaV1();
        INSTANCE = var0;
    }

   @Entity
   @Table(
      name = "asset",
      indexes = {@Index(columnList = "owner", name = "idx_asset_owner"), @Index(columnList = "cusip", name = "idx_asset_cusip")}
   )

    public static final class PersistentAsset extends PersistentState {


      @Column(name = "cusip") @NotNull private final String cusip;

      @Column(name = "asset_name") @NotNull private final String assetName;

      @Column(name = "purchase_cost") @NotNull private final String purchaseCost;

      @Column(name = "owner") @NotNull private final AbstractParty owner;


      @ElementCollection
      @Column(name = "participants")
      @CollectionTable(name = "asset_participants", joinColumns = {
              @JoinColumn(referencedColumnName = "output_index", name = "output_index"),
              @JoinColumn(referencedColumnName = "transaction_id", name = "transaction_id")
      }
      )

      @Nullable
      private Set<AbstractParty> participants;

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
      public final Set<AbstractParty> getParticipants() {
          return this.participants;
      }

      public final void setParticipants(Set<AbstractParty> participants) {
         this.participants = participants;
      }

      public PersistentAsset(@NotNull String cusip, @NotNull String assetName, @NotNull String purchaseCost,@NotNull AbstractParty owner, @NotNull Set<AbstractParty> participants)
      {
          super(null);
          Intrinsics.checkParameterIsNotNull(cusip, "cusip");
          Intrinsics.checkParameterIsNotNull(assetName, "assetName");
          Intrinsics.checkParameterIsNotNull(purchaseCost, "purchaseCost");
          Intrinsics.checkParameterIsNotNull(owner, "owner");

          this.cusip = cusip;
          this.assetName = assetName;
          this.purchaseCost = purchaseCost;
          this.owner = owner;
          this.participants = participants;

      }

       // Default constructor required by hibernate
       public PersistentAsset() {

           this.cusip = "default-constructor-required-for-hibernate";
           this.assetName = "";
           this.purchaseCost = "";
           this.owner =  NullKeys.INSTANCE.getNULL_PARTY();
           this.participants =(Set)(new LinkedHashSet());

       }
   }
}

