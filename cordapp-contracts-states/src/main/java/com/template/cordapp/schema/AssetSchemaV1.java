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
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.crypto.NullKeys;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.PersistentStateRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001\u0003B\u0007\b\u0002¢\u0006\u0002\u0010\u0002¨\u0006\u0004"},
   d2 = {"Lcom/synechron/cordapp/schema/AssetSchemaV1;", "Lnet/corda/core/schemas/MappedSchema;", "()V", "PersistentAsset", "cordapp-contracts-states"}
)
public final class AssetSchemaV1 extends MappedSchema {
   public static final AssetSchemaV1 INSTANCE;

   private AssetSchemaV1() {
      super(AssetSchema.INSTANCE.getClass(), 1, (Iterable)CollectionsKt.listOf(AssetSchemaV1.PersistentAsset.class));
      INSTANCE = (AssetSchemaV1)this;
   }

   static {
      new AssetSchemaV1();
   }

   @Entity
   @Table(
      name = "asset",
      indexes = {@Index(
   columnList = "owner",
   name = "idx_asset_owner"
), @Index(
   columnList = "cusip",
   name = "idx_asset_cusip"
)}
   )
   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\b\f\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0016¢\u0006\u0002\u0010\u0002B7\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0010\b\u0002\u0010\t\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\n¢\u0006\u0002\u0010\u000bR\u0016\u0010\u0005\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R&\u0010\t\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\n8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0016\u0010\u0006\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\r¨\u0006\u0016"},
      d2 = {"Lcom/synechron/cordapp/schema/AssetSchemaV1$PersistentAsset;", "Lnet/corda/core/schemas/PersistentState;", "()V", "cusip", "", "assetName", "purchaseCost", "owner", "Lnet/corda/core/identity/AbstractParty;", "participants", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lnet/corda/core/identity/AbstractParty;Ljava/util/Set;)V", "getAssetName", "()Ljava/lang/String;", "getCusip", "getOwner", "()Lnet/corda/core/identity/AbstractParty;", "getParticipants", "()Ljava/util/Set;", "setParticipants", "(Ljava/util/Set;)V", "getPurchaseCost", "cordapp-contracts-states"}
   )
   public static final class PersistentAsset extends PersistentState {
      @Column(
         name = "cusip"
      )
      @NotNull
      private final String cusip;
      @Column(
         name = "asset_name"
      )
      @NotNull
      private final String assetName;
      @Column(
         name = "purchase_cost"
      )
      @NotNull
      private final String purchaseCost;
      @Column(
         name = "owner"
      )
      @NotNull
      private final AbstractParty owner;
      @ElementCollection
      @Column(
         name = "participants"
      )
      @CollectionTable(
         name = "asset_participants",
         joinColumns = {@JoinColumn(
   referencedColumnName = "output_index",
   name = "output_index"
), @JoinColumn(
   referencedColumnName = "transaction_id",
   name = "transaction_id"
)}
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

      public final void setParticipants(@Nullable Set <set-?>) {
         this.participants = var1;
      }

      public PersistentAsset(@NotNull String cusip, @NotNull String assetName, @NotNull String purchaseCost, @NotNull AbstractParty owner, @Nullable Set participants) {
         Intrinsics.checkParameterIsNotNull(cusip, "cusip");
         Intrinsics.checkParameterIsNotNull(assetName, "assetName");
         Intrinsics.checkParameterIsNotNull(purchaseCost, "purchaseCost");
         Intrinsics.checkParameterIsNotNull(owner, "owner");
         super((PersistentStateRef)null, 1, (DefaultConstructorMarker)null);
         this.cusip = cusip;
         this.assetName = assetName;
         this.purchaseCost = purchaseCost;
         this.owner = owner;
         this.participants = participants;
      }

      // $FF: synthetic method
      public PersistentAsset(String var1, String var2, String var3, AbstractParty var4, Set var5, int var6, DefaultConstructorMarker var7) {
         if ((var6 & 16) != 0) {
            var5 = (Set)null;
         }

         this(var1, var2, var3, var4, var5);
      }

      public PersistentAsset() {
         AbstractParty var5 = (AbstractParty)NullKeys.INSTANCE.getNULL_PARTY();
         String var4 = "";
         String var3 = "";
         String var2 = "default-constructor-required-for-hibernate";
         Set var6 = (Set)(new LinkedHashSet());
         this(var2, var3, var4, var5, var6);
      }
   }
}

