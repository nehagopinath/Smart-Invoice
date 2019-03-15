package com.template.cordapp.state;

import com.template.cordapp.schema.AssetTransferSchemaV1;
import com.template.cordapp.schema.AssetTransferSchemaV1.PersistentAssetTransfer;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 1,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u001c\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u00012\u00020\u0002BI\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0006\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e¢\u0006\u0002\u0010\u000fJ\t\u0010\u001c\u001a\u00020\u0004HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0006HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0006HÆ\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\u0006HÆ\u0003J\t\u0010 \u001a\u00020\nHÆ\u0003J\u000f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00060\fHÆ\u0003J\t\u0010\"\u001a\u00020\u000eHÆ\u0003JW\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\t\u001a\u00020\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\f2\b\b\u0002\u0010\r\u001a\u00020\u000eHÆ\u0001J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010'HÖ\u0003J\u0010\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+H\u0016J\t\u0010,\u001a\u00020-HÖ\u0001J\u000e\u0010.\u001a\b\u0012\u0004\u0012\u00020+0/H\u0016J\t\u00100\u001a\u000201HÖ\u0001R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0014\u0010\r\u001a\u00020\u000eX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001a\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0013R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0013R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001b¨\u00062"},
   d2 = {"Lcom/synechron/cordapp/state/AssetTransfer;", "Lnet/corda/core/contracts/LinearState;", "Lnet/corda/core/schemas/QueryableState;", "asset", "Lcom/synechron/cordapp/state/Asset;", "securitySeller", "Lnet/corda/core/identity/AbstractParty;", "securityBuyer", "clearingHouse", "status", "Lcom/synechron/cordapp/state/RequestStatus;", "participants", "", "linearId", "Lnet/corda/core/contracts/UniqueIdentifier;", "(Lcom/synechron/cordapp/state/Asset;Lnet/corda/core/identity/AbstractParty;Lnet/corda/core/identity/AbstractParty;Lnet/corda/core/identity/AbstractParty;Lcom/synechron/cordapp/state/RequestStatus;Ljava/util/List;Lnet/corda/core/contracts/UniqueIdentifier;)V", "getAsset", "()Lcom/synechron/cordapp/state/Asset;", "getClearingHouse", "()Lnet/corda/core/identity/AbstractParty;", "getLinearId", "()Lnet/corda/core/contracts/UniqueIdentifier;", "getParticipants", "()Ljava/util/List;", "getSecurityBuyer", "getSecuritySeller", "getStatus", "()Lcom/synechron/cordapp/state/RequestStatus;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "", "generateMappedObject", "Lnet/corda/core/schemas/PersistentState;", "schema", "Lnet/corda/core/schemas/MappedSchema;", "hashCode", "", "supportedSchemas", "", "toString", "", "cordapp-contracts-states"}
)
public final class AssetTransfer implements LinearState, QueryableState {
   @NotNull
   private final Asset asset;
   @NotNull
   private final AbstractParty securitySeller;
   @NotNull
   private final AbstractParty securityBuyer;
   @Nullable
   private final AbstractParty clearingHouse;
   @NotNull
   private final RequestStatus status;
   @NotNull
   private final List participants;
   @NotNull
   private final UniqueIdentifier linearId;

   @NotNull
   public PersistentState generateMappedObject(@NotNull MappedSchema schema) {
      Intrinsics.checkParameterIsNotNull(schema, "schema");
      if (schema instanceof AssetTransferSchemaV1) {
         return (PersistentState)(new PersistentAssetTransfer(this.asset.getCusip(), this.securitySeller, this.securityBuyer, this.clearingHouse, this.status.getValue(), CollectionsKt.toMutableSet((Iterable)this.getParticipants()), this.getLinearId().toString()));
      } else {
         throw (Throwable)(new IllegalArgumentException("Unrecognised schema " + schema));
      }
   }

   @NotNull
   public Iterable supportedSchemas() {
      return (Iterable)SetsKt.setOf(AssetTransferSchemaV1.INSTANCE);
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
   public final RequestStatus getStatus() {
      return this.status;
   }

   @NotNull
   public List getParticipants() {
      return this.participants;
   }

   @NotNull
   public UniqueIdentifier getLinearId() {
      return this.linearId;
   }

   public AssetTransfer(@NotNull Asset asset, @NotNull AbstractParty securitySeller, @NotNull AbstractParty securityBuyer, @Nullable AbstractParty clearingHouse, @NotNull RequestStatus status, @NotNull List participants, @NotNull UniqueIdentifier linearId) {
      Intrinsics.checkParameterIsNotNull(asset, "asset");
      Intrinsics.checkParameterIsNotNull(securitySeller, "securitySeller");
      Intrinsics.checkParameterIsNotNull(securityBuyer, "securityBuyer");
      Intrinsics.checkParameterIsNotNull(status, "status");
      Intrinsics.checkParameterIsNotNull(participants, "participants");
      Intrinsics.checkParameterIsNotNull(linearId, "linearId");
      super();
      this.asset = asset;
      this.securitySeller = securitySeller;
      this.securityBuyer = securityBuyer;
      this.clearingHouse = clearingHouse;
      this.status = status;
      this.participants = participants;
      this.linearId = linearId;
   }

   // $FF: synthetic method
   public AssetTransfer(Asset var1, AbstractParty var2, AbstractParty var3, AbstractParty var4, RequestStatus var5, List var6, UniqueIdentifier var7, int var8, DefaultConstructorMarker var9) {
      if ((var8 & 32) != 0) {
         var6 = CollectionsKt.listOf(new AbstractParty[]{var3, var2});
      }

      if ((var8 & 64) != 0) {
         var7 = new UniqueIdentifier((String)null, (UUID)null, 3, (DefaultConstructorMarker)null);
      }

      this(var1, var2, var3, var4, var5, var6, var7);
   }

   @NotNull
   public final Asset component1() {
      return this.asset;
   }

   @NotNull
   public final AbstractParty component2() {
      return this.securitySeller;
   }

   @NotNull
   public final AbstractParty component3() {
      return this.securityBuyer;
   }

   @Nullable
   public final AbstractParty component4() {
      return this.clearingHouse;
   }

   @NotNull
   public final RequestStatus component5() {
      return this.status;
   }

   @NotNull
   public final List component6() {
      return this.getParticipants();
   }

   @NotNull
   public final UniqueIdentifier component7() {
      return this.getLinearId();
   }

   @NotNull
   public final AssetTransfer copy(@NotNull Asset asset, @NotNull AbstractParty securitySeller, @NotNull AbstractParty securityBuyer, @Nullable AbstractParty clearingHouse, @NotNull RequestStatus status, @NotNull List participants, @NotNull UniqueIdentifier linearId) {
      Intrinsics.checkParameterIsNotNull(asset, "asset");
      Intrinsics.checkParameterIsNotNull(securitySeller, "securitySeller");
      Intrinsics.checkParameterIsNotNull(securityBuyer, "securityBuyer");
      Intrinsics.checkParameterIsNotNull(status, "status");
      Intrinsics.checkParameterIsNotNull(participants, "participants");
      Intrinsics.checkParameterIsNotNull(linearId, "linearId");
      return new AssetTransfer(asset, securitySeller, securityBuyer, clearingHouse, status, participants, linearId);
   }

   // $FF: synthetic method
   // $FF: bridge method
   @NotNull
   public static AssetTransfer copy$default(AssetTransfer var0, Asset var1, AbstractParty var2, AbstractParty var3, AbstractParty var4, RequestStatus var5, List var6, UniqueIdentifier var7, int var8, Object var9) {
      if ((var8 & 1) != 0) {
         var1 = var0.asset;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.securitySeller;
      }

      if ((var8 & 4) != 0) {
         var3 = var0.securityBuyer;
      }

      if ((var8 & 8) != 0) {
         var4 = var0.clearingHouse;
      }

      if ((var8 & 16) != 0) {
         var5 = var0.status;
      }

      if ((var8 & 32) != 0) {
         var6 = var0.getParticipants();
      }

      if ((var8 & 64) != 0) {
         var7 = var0.getLinearId();
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7);
   }

   public String toString() {
      return "AssetTransfer(asset=" + this.asset + ", securitySeller=" + this.securitySeller + ", securityBuyer=" + this.securityBuyer + ", clearingHouse=" + this.clearingHouse + ", status=" + this.status + ", participants=" + this.getParticipants() + ", linearId=" + this.getLinearId() + ")";
   }

   public int hashCode() {
      Asset var10000 = this.asset;
      int var1 = (var10000 != null ? var10000.hashCode() : 0) * 31;
      AbstractParty var10001 = this.securitySeller;
      var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
      var10001 = this.securityBuyer;
      var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
      var10001 = this.clearingHouse;
      var1 = (var1 + (var10001 != null ? var10001.hashCode() : 0)) * 31;
      RequestStatus var2 = this.status;
      var1 = (var1 + (var2 != null ? var2.hashCode() : 0)) * 31;
      List var3 = this.getParticipants();
      var1 = (var1 + (var3 != null ? var3.hashCode() : 0)) * 31;
      UniqueIdentifier var4 = this.getLinearId();
      return var1 + (var4 != null ? var4.hashCode() : 0);
   }

   public boolean equals(Object var1) {
      if (this != var1) {
         if (var1 instanceof AssetTransfer) {
            AssetTransfer var2 = (AssetTransfer)var1;
            if (Intrinsics.areEqual(this.asset, var2.asset) && Intrinsics.areEqual(this.securitySeller, var2.securitySeller) && Intrinsics.areEqual(this.securityBuyer, var2.securityBuyer) && Intrinsics.areEqual(this.clearingHouse, var2.clearingHouse) && Intrinsics.areEqual(this.status, var2.status) && Intrinsics.areEqual(this.getParticipants(), var2.getParticipants()) && Intrinsics.areEqual(this.getLinearId(), var2.getLinearId())) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }
}

