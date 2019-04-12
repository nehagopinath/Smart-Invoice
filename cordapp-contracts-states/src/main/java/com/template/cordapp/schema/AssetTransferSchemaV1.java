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


public final class AssetTransferSchemaV1 extends MappedSchema {

    public static final AssetTransferSchemaV1 INSTANCE;

   public AssetTransferSchemaV1() {
       super(AssetTransferSchema.INSTANCE.getClass(), 1, CollectionsKt.listOf(PersistentAssetTransfer.class));
   }

    static {
        AssetTransferSchemaV1 var0 = new AssetTransferSchemaV1();
        INSTANCE = var0;
    }

   @Entity
   @Table(name = "asset_transfer",
           indexes = {
           @Index(columnList = "linear_id", name = "idx_asset_transfer_linearId"),
                   @Index(columnList = "cusip", name = "idx_asset_transfer_cusip")
   })

   public static final class PersistentAssetTransfer extends PersistentState {
      @Column(
         name = "cusip"
      )
      @NotNull public String cusip;

      @Column(
         name = "lender_of_security"
      )
      @NotNull public AbstractParty securitySeller;

      @Column(
         name = "lender_of_cash"
      )
      @NotNull public AbstractParty securityBuyer;

      @Column(
         name = "clearing_house"
      )
      @Nullable public AbstractParty clearingHouse;

      @Column(
         name = "status"
      )
      @NotNull public String status;

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
      public Set<AbstractParty> participants;
      @Column(
         name = "linear_id"
      )

      @NotNull public String linearId;

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
      public final Set<AbstractParty> getParticipants() {
         return this.participants;
      }


      public final void setParticipants(@Nullable Set<AbstractParty> participants) {
           this.participants = participants;
       }

       @NotNull public final String getLinearId() {
         return this.linearId;
      }

      public PersistentAssetTransfer(@NotNull String cusip, @NotNull AbstractParty securitySeller, @NotNull AbstractParty securityBuyer, @Nullable AbstractParty clearingHouse, @NotNull String status, @Nullable Set<AbstractParty> participants, @NotNull String linearId) {

          super(null);
          Intrinsics.checkParameterIsNotNull(cusip, "cusip");
          Intrinsics.checkParameterIsNotNull(securitySeller, "securitySeller");
          Intrinsics.checkParameterIsNotNull(securityBuyer, "securityBuyer");
          Intrinsics.checkParameterIsNotNull(status, "status");
          Intrinsics.checkParameterIsNotNull(linearId, "linearId");
          this.cusip = cusip;
          this.securitySeller = securitySeller;
          this.securityBuyer = securityBuyer;
          this.clearingHouse = clearingHouse;
          this.status = status;
          this.participants = participants;
          this.linearId = linearId;
      }

       public PersistentAssetTransfer(){
           this.cusip = "default-constructor-required-for-hibernate";
           this.securitySeller = NullKeys.INSTANCE.getNULL_PARTY();
           this.securityBuyer = NullKeys.INSTANCE.getNULL_PARTY();
           this.clearingHouse = NullKeys.INSTANCE.getNULL_PARTY();
           this.status = "";
           this.participants = (Set)(new LinkedHashSet());
           this.linearId = "";
       }
   }
}

