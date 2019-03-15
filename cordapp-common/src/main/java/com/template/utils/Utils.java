// UtilsKt.java
package com.synechron.cordapp.utils;

import com.synechron.cordapp.state.Asset;
import java.util.Collection;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.VaultService;
import net.corda.core.node.services.Vault.StateStatus;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.PageSpecification;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.node.services.vault.Sort;
import net.corda.core.node.services.vault.CriteriaExpression.ColumnPredicateExpression;
import net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 2,
   d1 = {"\u0000\u0016\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a\u0018\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005¨\u0006\u0006"},
   d2 = {"getAssetByCusip", "Lnet/corda/core/contracts/StateAndRef;", "Lcom/synechron/cordapp/state/Asset;", "Lnet/corda/core/node/ServiceHub;", "cusip", "", "cordapp-common"}
)
public final class UtilsKt {
   @NotNull
   public static final StateAndRef getAssetByCusip(@NotNull ServiceHub $receiver, @NotNull String cusip) {
      Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
      Intrinsics.checkParameterIsNotNull(cusip, "cusip");
      ColumnPredicateExpression cusipExpr = Builder.INSTANCE.equal(UtilsKt$getAssetByCusip$cusipExpr$1.INSTANCE, cusip);
      VaultCustomQueryCriteria cusipCriteria = new VaultCustomQueryCriteria((CriteriaExpression)cusipExpr, (StateStatus)null, (Set)null, 6, (DefaultConstructorMarker)null);
      VaultService $receiver$iv = $receiver.getVaultService();
      StateAndRef var10000 = (StateAndRef)CollectionsKt.singleOrNull($receiver$iv._queryBy((QueryCriteria)cusipCriteria, new PageSpecification(0, 0, 3, (DefaultConstructorMarker)null), new Sort((Collection)SetsKt.emptySet()), Asset.class).getStates());
      if (var10000 != null) {
         return var10000;
      } else {
         throw (Throwable)(new FlowException("Asset with id " + cusip + " not found."));
      }
   }
}
// UtilsKt$getAssetByCusip$cusipExpr$1.java
package com.synechron.cordapp.utils;

import com.synechron.cordapp.schema.AssetSchemaV1.PersistentAsset;
import kotlin.Metadata;
import kotlin.jvm.internal.PropertyReference1;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;
import kotlin.reflect.KProperty1;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 8},
   bv = {1, 0, 2},
   k = 3
)
final class UtilsKt$getAssetByCusip$cusipExpr$1 extends PropertyReference1 {
   public static final KProperty1 INSTANCE = new UtilsKt$getAssetByCusip$cusipExpr$1();

   public String getName() {
      return "cusip";
   }

   public String getSignature() {
      return "getCusip()Ljava/lang/String;";
   }

   public KDeclarationContainer getOwner() {
      return Reflection.getOrCreateKotlinClass(PersistentAsset.class);
   }

   @Nullable
   public Object get(@Nullable Object receiver) {
      return ((PersistentAsset)receiver).getCusip();
   }
}

