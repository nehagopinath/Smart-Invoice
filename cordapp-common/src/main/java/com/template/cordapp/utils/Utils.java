package com.template.cordapp.utils;

import com.template.cordapp.schema.AssetSchemaV1;
import com.template.cordapp.state.Asset;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.*;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.VaultService;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.CriteriaExpression.ColumnPredicateExpression;
import net.corda.core.node.services.vault.PageSpecification;
import net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria;
import net.corda.core.node.services.vault.Sort;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class Utils {
   @NotNull
   public static final StateAndRef getAssetByCusip (@NotNull ServiceHub receiver, @NotNull String cusip) throws FlowException{

      Intrinsics.checkParameterIsNotNull(receiver, "receiver");
      Intrinsics.checkParameterIsNotNull(cusip, "cusip");

      ColumnPredicateExpression cusipExpr = Builder.INSTANCE.equal(UtilsKt.INSTANCE, cusip);
      VaultCustomQueryCriteria cusipCriteria = new VaultCustomQueryCriteria(cusipExpr);
      VaultService vaultService = receiver.getVaultService();
      StateAndRef ser = CollectionsKt.singleOrNull(vaultService._queryBy(cusipCriteria,
              new PageSpecification(0, 0), new Sort((Collection) SetsKt.emptySet()),
              Asset.class).getStates());

      if (ser != null) {
         return ser;
      } else {
         throw (new FlowException("Asset with id " + cusip + " not found."));
      }
   }
}


final class UtilsKt extends PropertyReference1{


   public static final KProperty1 INSTANCE = new UtilsKt();


   public String getName() {
      return "cusip";
   }

   public String getSignature() {
      return "getCusip()Ljava/lang/String;";
   }

   public KDeclarationContainer getOwner() {
      return Reflection.getOrCreateKotlinClass(AssetSchemaV1.PersistentAsset.class);
   }

   @Nullable
   public Object get(@Nullable Object receiver) {
      return ((AssetSchemaV1.PersistentAsset)receiver).getCusip();
   }

}




