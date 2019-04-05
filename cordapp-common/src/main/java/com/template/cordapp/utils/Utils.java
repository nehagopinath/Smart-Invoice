package com.template.cordapp.utils;

import com.template.cordapp.state.Asset;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.VaultService;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression.ColumnPredicateExpression;
import net.corda.core.node.services.vault.PageSpecification;
import net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria;
import net.corda.core.node.services.vault.Sort;
import java.util.Collection;

public final class Utils {

   //todo 9  : Resolve this class

   public static final StateAndRef getAssetByCusip (ServiceHub receiver, String cusip) throws FlowException{

      ColumnPredicateExpression cusipExpr = Builder.INSTANCE.equal(UtilsKt.INSTANCE, cusip);
      VaultCustomQueryCriteria cusipCriteria = new VaultCustomQueryCriteria(cusipExpr, null, null);
      VaultService vaultService = receiver.getVaultService();
      StateAndRef ser = (StateAndRef) CollectionsKt.singleOrNull(vaultService._queryBy(cusipCriteria,
              new PageSpecification(0, 0), new Sort((Collection) SetsKt.emptySet()),
              Asset.class).getStates());

      if (ser != null) {
         return ser;
      } else {
         throw (new FlowException("Asset with id " + cusip + " not found."));
      }
   }
}

/*final class UtilsKt extends PropertyReference1 {
   public static final KProperty1 INSTANCE = new UtilsKt();

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
}*/




