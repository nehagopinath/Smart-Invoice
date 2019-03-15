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
   d2 = {"Lcom/synechron/cordapp/schema/AssetTransferSchemaV1;", "Lnet/corda/core/schemas/MappedSchema;", "()V", "PersistentAssetTransfer", "cordapp-contracts-states"}
)
public final class AssetTransferSchemaV1 extends MappedSchema {
   public static final AssetTransferSchemaV1 INSTANCE;

   private AssetTransferSchemaV1() {
      super(AssetSchema.INSTANCE.getClass(), 1, (Iterable)CollectionsKt.listOf(AssetTransferSchemaV1.PersistentAssetTransfer.class));
      INSTANCE = (AssetTransferSchemaV1)this;
   }

   static {
      new AssetTransferSchemaV1();
   }

   @Entity
   @Table(
      name = "asset_transfer",
      indexes = {@Index(
   columnList = "linear_id",
   name = "idx_asset_transfer_linearId"
), @Index(
   columnList = "cusip",
   name = "idx_asset_transfer_cusip"
)}
   )
   @Metadata(
      mv = {1, 1, 8},
      bv = {1, 0, 2},
      k = 1,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010#\n\u0002\b\u000f\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0016¢\u0006\u0002\u0010\u0002BI\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\t\u001a\u00020\u0004\u0012\u0010\b\u0002\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u000b\u0012\u0006\u0010\f\u001a\u00020\u0004¢\u0006\u0002\u0010\rR\u0018\u0010\b\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\f\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R&\u0010\n\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u000b8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u0016\u0010\u0007\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000fR\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000fR\u0016\u0010\t\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0011¨\u0006\u001a"},
      d2 = {"Lcom/synechron/cordapp/schema/AssetTransferSchemaV1$PersistentAssetTransfer;", "Lnet/corda/core/schemas/PersistentState;", "()V", "cusip", "", "securitySeller", "Lnet/corda/core/identity/AbstractParty;", "securityBuyer", "clearingHouse", "status", "participants", "", "linearId", "(Ljava/lang/String;Lnet/corda/core/identity/AbstractParty;Lnet/corda/core/identity/AbstractParty;Lnet/corda/core/identity/AbstractParty;Ljava/lang/String;Ljava/util/Set;Ljava/lang/String;)V", "getClearingHouse", "()Lnet/corda/core/identity/AbstractParty;", "getCusip", "()Ljava/lang/String;", "getLinearId", "getParticipants", "()Ljava/util/Set;", "setParticipants", "(Ljava/util/Set;)V", "getSecurityBuyer", "getSecuritySeller", "getStatus", "cordapp-contracts-states"}
   )
   public static final class PersistentAssetTransfer extends PersistentState {
      @Column(
         name = "cusip"
      )
      @NotNull
      private final String cusip;
      @Column(
         name = "lender_of_security"
      )
      @NotNull
      private final AbstractParty securitySeller;
      @Column(
         name = "lender_of_cash"
      )
      @NotNull
      private final AbstractParty securityBuyer;
      @Column(
         name = "clearing_house"
      )
      @Nullable
      private final AbstractParty clearingHouse;
      @Column(
         name = "status"
      )
      @NotNull
      private final String status;
      @ElementCollection
      @Column(
         name = "participants"
      )
      @CollectionTable(
         name = "asset_transfer_participants",
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
      @Column(
         name = "linear_id"
      )
      @NotNull
      private final String linearId;

      @NotNull
      public final String getCusip() {
         return this.cusip;
      }

      @NotNull
      public final AbstractParty getSecuritySeller() {
         return this.securitySeller;
      }

      @NotNull
      public final AbstractParty getSecurityBuyer() {
         return this.securityBuyer;
      }

      @Nullable
      public final AbstractParty getClearingHouse() {
         return this.clearingHouse;
      }

      @NotNull
      public final String getStatus() {
         return this.status;
      }

      @Nullable
      public final Set getParticipants() {
         return this.participants;
      }

      public final void setParticipants(@Nullable Set <set-?>) {
         this.participants = var1;
      }

      @NotNull
      public final String getLinearId() {
         return this.linearId;
      }

      public PersistentAssetTransfer(@NotNull String cusip, @NotNull AbstractParty securitySeller, @NotNull AbstractParty securityBuyer, @Nullable AbstractParty clearingHouse, @NotNull String status, @Nullable Set participants, @NotNull String linearId) {
         Intrinsics.checkParameterIsNotNull(cusip, "cusip");
         Intrinsics.checkParameterIsNotNull(securitySeller, "securitySeller");
         Intrinsics.checkParameterIsNotNull(securityBuyer, "securityBuyer");
         Intrinsics.checkParameterIsNotNull(status, "status");
         Intrinsics.checkParameterIsNotNull(linearId, "linearId");
         super((PersistentStateRef)null, 1, (DefaultConstructorMarker)null);
         this.cusip = cusip;
         this.securitySeller = securitySeller;
         this.securityBuyer = securityBuyer;
         this.clearingHouse = clearingHouse;
         this.status = status;
         this.participants = participants;
         this.linearId = linearId;
      }

      // $FF: synthetic method
      public PersistentAssetTransfer(String var1, AbstractParty var2, AbstractParty var3, AbstractParty var4, String var5, Set var6, String var7, int var8, DefaultConstructorMarker var9) {
         if ((var8 & 32) != 0) {
            var6 = (Set)null;
         }

         this(var1, var2, var3, var4, var5, var6, var7);
      }

      public PersistentAssetTransfer() {
         AbstractParty var10002 = (AbstractParty)NullKeys.INSTANCE.getNULL_PARTY();
         AbstractParty var10003 = (AbstractParty)NullKeys.INSTANCE.getNULL_PARTY();
         AbstractParty var10004 = (AbstractParty)NullKeys.INSTANCE.getNULL_PARTY();
         String var6 = "";
         AbstractParty var5 = var10004;
         AbstractParty var4 = var10003;
         AbstractParty var3 = var10002;
         String var2 = "default-constructor-required-for-hibernate";
         Set var7 = (Set)(new LinkedHashSet());
         this(var2, var3, var4, var5, var6, var7, "");
      }
   }
}

