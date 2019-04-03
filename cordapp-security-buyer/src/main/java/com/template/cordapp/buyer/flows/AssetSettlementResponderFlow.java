package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.clearinghouse.flows.AssetSettlementInitiatorFlow;
import com.template.cordapp.common.exception.TooManyStatesFoundException;
import com.template.cordapp.common.flows.IdentitySyncFlowReceive;
import com.template.cordapp.common.flows.IdentitySyncFlowSend;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.state.AssetTransfer;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import net.corda.core.flows.*;
import net.corda.core.node.StatesToRecord;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.UntrustworthyData;
import net.corda.finance.contracts.asset.Cash;
import java.security.SignatureException;
import java.util.*;


@InitiatedBy(AssetSettlementInitiatorFlow.class)

public final class AssetSettlementResponderFlow extends FlowLogic<SignedTransaction> {

   private final ProgressTracker progressTracker;
   private final FlowSession otherSideSession;

   public AssetSettlementResponderFlow(ProgressTracker progressTracker, FlowSession otherSideSession) {
      this.progressTracker = progressTracker;
      this.otherSideSession = otherSideSession;
   }

   public ProgressTracker getProgressTracker() {
      return this.progressTracker;
   }

   @Suspendable
   @Override
   public SignedTransaction call() throws FlowException {
      SignedTransaction ptx1 = (SignedTransaction)this.subFlow((FlowLogic)(new ReceiveTransactionFlow(this.otherSideSession, false, StatesToRecord.NONE)));

      //TODO check : not sure if this works
      LedgerTransaction ltx1 = null;
      try {
         ltx1 = ptx1.toLedgerTransaction(this.getServiceHub(), false);
      } catch (SignatureException e) {
         e.printStackTrace();
      }


      Iterable inputstates = (Iterable)ltx1.getInputStates();
      Collection destinationAT = (Collection)(new ArrayList());
      Iterator instIterator = inputstates.iterator();

      while(instIterator.hasNext()) {
         Object o = instIterator.next();
         if (o instanceof AssetTransfer) {
            destinationAT.add(o);
         }
      }

      //throw too many states found exception if this fails
      AssetTransfer assetTransfer = (AssetTransfer) CollectionsKt.singleOrNull((List) destinationAT);

      if (assetTransfer == null){
          throw (new TooManyStatesFoundException("Transaction with more than one `AssetTransfer` " + "input states received from `" + this.otherSideSession.getCounterparty() + "` party"));
      }

      //TODO Using initiating flow id to soft lock reserve the Cash state.

       FlowSession flowSession = this.otherSideSession;
       UntrustworthyData<UUID> recieveiv = flowSession.receive(UUID.class);
       UUID it = (UUID)recieveiv.getFromUntrustedWorld();

       //Issue cash to security owner i.e. `Seller` party.

       Pair AB = (Pair) Cash.generateSpend(this.getServiceHub(),
               new TransactionBuilder(ltx1.getNotary(),it,null,null,null,null,null,null), //soft reserve the cash state.
               assetTransfer.getAsset().getPurchaseCost(),
               this.getOurIdentityAndCert(),
               assetTransfer.getSecuritySeller(),
               null);

       TransactionBuilder txbWithCash = (TransactionBuilder) AB.component1();
       List cashSignKeys = (List)AB.component2();

       SignedTransaction ptx2 = this.getServiceHub().signInitialTransaction(txbWithCash);

       subFlow((FlowLogic)(new IdentitySyncFlowSend(Collections.singleton(this), ptx2.getTx())));
       subFlow((FlowLogic)(new SendTransactionFlow(this.otherSideSession, ptx2)));
       subFlow((FlowLogic)(new IdentitySyncFlowReceive(this.otherSideSession)));

       SignedTransaction stx = (SignedTransaction) subFlow(new SignTxFlow(this.otherSideSession));

       return waitForLedgerCommit(stx.getId());


   }

   public final FlowSession getOtherSideSession() {
      return this.otherSideSession;
   }

   public AssetSettlementResponderFlow( FlowSession otherSideSession) {
      super();
      this.otherSideSession = otherSideSession;
      this.progressTracker = new ProgressTracker();
   }

}

