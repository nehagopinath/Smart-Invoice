// UtilsKt.java
package com.template.cordapp.utils;

import java.util.Collection;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
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
//ToDo: Find way to import from other places than Common



public final class Utils {

   public static final StateAndRef getAssetByCusip (ServiceHub receiver, String cusip) throws FlowException{

      ColumnPredicateExpression cusipExpr = null; //wrong, want to bind solution
      VaultCustomQueryCriteria cusipCriteria = new VaultCustomQueryCriteria(cusipExpr, null, null);
      VaultService receiveriv = receiver.getVaultService();
      StateAndRef ser = null;


     //CollectionsKt.singleOrNull(receiveriv._queryBy((QueryCriteria)cusipCriteria, new PageSpecification(), new Sort((SetsKt.emptySet()),null).getStates()));

      if (ser != null) {
         return ser;
      } else {
         throw (new FlowException("Asset with id " + cusip + " not found."));
      }
   }
}



