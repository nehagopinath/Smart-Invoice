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


public final class AssetTransferSchemaV1 extends MappedSchema {

   private AssetTransferSchemaV1() {
       super(AssetTransferSchema.class, 1, ImmutableList.of(PersistentAssetTransfer.class));
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
      private String cusip;

      @Column(
         name = "lender_of_security"
      )
      private AbstractParty securitySeller;

      @Column(
         name = "lender_of_cash"
      )
      private AbstractParty securityBuyer;

      @Column(
         name = "clearing_house"
      )
      private AbstractParty clearingHouse;

      @Column(
         name = "status"
      )
      private String status;

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

      private String linearId;


      public final String getCusip() {
         return this.cusip;
      }


      public final AbstractParty getSecuritySeller() {
         return this.securitySeller;
      }


      public final AbstractParty getSecurityBuyer() {
         return this.securityBuyer;
      }


      public final AbstractParty getClearingHouse() {
         return this.clearingHouse;
      }


      public final String getStatus() {
         return this.status;
      }

      @Nullable
      public final Set getParticipants() {
         return this.participants;
      }

      /*public final void setParticipants(@Nullable Set <set-?>) {
         this.participants = var1;
      }*/

      public final String getLinearId() {
         return this.linearId;
      }

      public PersistentAssetTransfer(@NotNull String cusip, @NotNull AbstractParty securitySeller, @NotNull AbstractParty securityBuyer, @Nullable AbstractParty clearingHouse, @NotNull String status, @Nullable Set participants, @NotNull String linearId) {
         this.cusip = cusip;
         this.securitySeller = securitySeller;
         this.securityBuyer = securityBuyer;
         this.clearingHouse = clearingHouse;
         this.status = status;
         this.participants = participants;
         this.linearId = linearId;
      }

       public PersistentAssetTransfer(){
          this.cusip = null;
           this.securitySeller = null;
           this.securityBuyer = null;
           this.clearingHouse = null;
           this.status = null;
           this.participants = null;
           this.linearId = null;
       }
   }
}

