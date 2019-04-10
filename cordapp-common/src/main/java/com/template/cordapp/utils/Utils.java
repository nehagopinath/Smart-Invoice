package com.template.cordapp.utils;

import com.template.cordapp.state.Asset;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.reflect.*;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.VaultService;
import net.corda.core.node.services.vault.Builder;
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


public final class Utils {

   public static final StateAndRef getAssetByCusip (ServiceHub receiver, String cusip) throws FlowException{

     ColumnPredicateExpression cusipExpr = Builder.INSTANCE.equal(UtilsKt.INSTANCE, cusip);
      VaultCustomQueryCriteria cusipCriteria = new VaultCustomQueryCriteria(cusipExpr, null, null);
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


final class UtilsKt implements KProperty1 {

   public static final KProperty1 INSTANCE = new UtilsKt();


   @Override
   public Getter getGetter() {
      return null;
   }

   @Override
   public Object get(Object o) {
      return null;
   }

   @Override
   public Object getDelegate(Object o) {
      return null;
   }

   @Override
   public Object invoke(Object o) {
      return null;
   }

   @Override
   public boolean isConst() {
      return false;
   }

   @Override
   public boolean isLateinit() {
      return false;
   }

   @Override
   public boolean isAbstract() {
      return false;
   }

   @Override
   public boolean isFinal() {
      return false;
   }

   @Override
   public boolean isOpen() {
      return false;
   }

   @Override
   public String getName() {
      return null;
   }


   @Override
   public List<KParameter> getParameters() {
      return null;
   }


   @Override
   public KType getReturnType() {
      return null;
   }


   @Override
   public List<KTypeParameter> getTypeParameters() {
      return null;
   }

   @Override
   public KVisibility getVisibility() {
      return null;
   }

   @Override
   public Object call(@NotNull Object... objects) {
      return null;
   }

   @Override
   public Object callBy(@NotNull Map map) {
      return null;
   }

   @Override
   public List<Annotation> getAnnotations() {
      return null;
   }
}




