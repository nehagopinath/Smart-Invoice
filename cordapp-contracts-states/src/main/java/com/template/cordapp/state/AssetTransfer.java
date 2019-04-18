package com.template.cordapp.state;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

import com.template.cordapp.schema.AssetTransferSchemaV1;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/**
 * This state acting as deal data before the actual [Asset] being transfer to target buyer party on settlement.
 */


public final class AssetTransfer implements LinearState, QueryableState {

   @NotNull
   private final Asset asset;
   @NotNull
   private AbstractParty securitySeller;
   @NotNull
   private AbstractParty securityBuyer;
   @Nullable
   private AbstractParty clearingHouse;
   @NotNull
   private final RequestStatus status;
   @NotNull
   private final List<AbstractParty> participants;
   @NotNull
   private final UniqueIdentifier linearId;


   public AssetTransfer(@NotNull Asset asset, @NotNull AbstractParty securitySeller, @NotNull AbstractParty securityBuyer, @Nullable AbstractParty clearingHouse, @NotNull RequestStatus status, @NotNull List<AbstractParty>  participants, @NotNull UniqueIdentifier linearId) {

      super();

      this.asset = asset;
      this.securitySeller = securitySeller;
      this.securityBuyer = securityBuyer;
      this.clearingHouse = clearingHouse;
      this.status = status;
      this.participants = Arrays.asList(securityBuyer, securitySeller);
      this.linearId = new UniqueIdentifier();
   }

    @NotNull
   public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
      Intrinsics.checkParameterIsNotNull(schema, "schema");
      if (schema instanceof com.template.cordapp.schema.AssetTransferSchemaV1) {
         return (new AssetTransferSchemaV1.PersistentAssetTransfer(
                 this.asset.getCusip(),
                 this.securitySeller,
                 this.securityBuyer,
                 this.clearingHouse,
                 this.status.getValue(),
                 CollectionsKt.toMutableSet((Iterable)this.getParticipants()),
                 this.getLinearId().toString()));
      } else {
         throw (new IllegalArgumentException("Unrecognised schema " + schema));
      }
   }

   @NotNull
   public Iterable<MappedSchema> supportedSchemas() {
      return ImmutableList.of(AssetTransferSchemaV1.INSTANCE);
   }


   @NotNull
   public final Asset getAsset() {
      return this.asset;
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
      return this.status.getValue();
   }

   @NotNull
   public List<AbstractParty> getParticipants() {
      return this.participants;
   }

   @NotNull
   public UniqueIdentifier getLinearId() {
      return this.linearId;
   }

   @NotNull
   public final AssetTransfer copy(@NotNull Asset asset, @NotNull AbstractParty securitySeller, @NotNull AbstractParty securityBuyer, @Nullable AbstractParty clearingHouse, @NotNull RequestStatus status, @NotNull List<AbstractParty> participants, @NotNull UniqueIdentifier linearId) {
      Intrinsics.checkParameterIsNotNull(asset, "asset");
      Intrinsics.checkParameterIsNotNull(securitySeller, "securitySeller");
      Intrinsics.checkParameterIsNotNull(securityBuyer, "securityBuyer");
      Intrinsics.checkParameterIsNotNull(status, "status");
      Intrinsics.checkParameterIsNotNull(participants, "participants");
      Intrinsics.checkParameterIsNotNull(linearId, "linearId");
      return new AssetTransfer(asset, securitySeller, securityBuyer, clearingHouse, status, participants, linearId);
   }


   public String toString() {
      return "AssetTransfer(asset=" + this.asset + ", securitySeller=" + this.securitySeller + ", securityBuyer=" + this.securityBuyer + ", clearingHouse=" + this.clearingHouse + ", status=" + this.status + ", participants=" + this.getParticipants() + ", linearId=" + this.getLinearId() + ")";
   }

   public boolean equals(Object var1) {
      if (this != var1) {
         if (var1 instanceof AssetTransfer) {
            AssetTransfer var2 = (AssetTransfer)var1;
            if (Intrinsics.areEqual(this.asset, var2.asset) &&
                    Intrinsics.areEqual(this.securitySeller, var2.securitySeller) &&
                    Intrinsics.areEqual(this.securityBuyer, var2.securityBuyer) &&
                    Intrinsics.areEqual(this.clearingHouse, var2.clearingHouse) &&
                    Intrinsics.areEqual(this.status, var2.status) &&
                    Intrinsics.areEqual(this.getParticipants(), var2.getParticipants()) &&
                    Intrinsics.areEqual(this.getLinearId(), var2.getLinearId())) {
               return true;
            }
         }
         return false;
      } else {
         return true;
      }
   }
}

