package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.exception.TooManyStatesFoundException;
import com.template.cordapp.common.flows.IdentitySyncFlow;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.flows.AbstractAssetSettlementFlow;
import com.template.cordapp.state.AssetTransfer;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import net.corda.core.contracts.PrivacySalt;
import net.corda.core.contracts.TimeWindow;
import net.corda.core.flows.*;
import net.corda.core.node.StatesToRecord;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.UntrustworthyData;
import net.corda.finance.contracts.asset.Cash;

import java.security.PublicKey;
import java.security.SignatureException;
import java.util.*;

//TODO : Fix this
//@InitiatedBy(AbstractAssetSettlementFlow.class)
public final class AssetSettlementResponderFlow extends FlowLogic {

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

      //TODO throw too many states found exception if this fails

      AssetTransfer assetTransfer = (AssetTransfer) CollectionsKt.singleOrNull((List) destinationAT);

      //TODO Using initiating flow id to soft lock reserve the Cash state.
       //val initiatingFlowId = otherSideSession.receive<UUID>().unwrap { it }
/*
       FlowSession flowSession = this.otherSideSession;
       UntrustworthyData<UUID> recieveiv = flowSession.receive(UUID.class);
       UUID it = (UUID)recieveiv.getFromUntrustedWorld(); */

       //Issue cash to security owner i.e. `Seller` party.

       Pair AB = Cash.generateSpend(this.getServiceHub(),
               new TransactionBuilder(ltx1.getNotary(), initiatingFlowId, (List)null, (List)null, (List)null, (List)null, (TimeWindow)null, (PrivacySalt)null),
               assetTransfer.getAsset().getPurchaseCost(),
               this.getOurIdentityAndCert(),
               assetTransfer.getSecuritySeller());

       TransactionBuilder txbWithCash = (TransactionBuilder) AB.component1();
       List cashSignKeys = (List)AB.component2();

       SignedTransaction ptx2 = this.getServiceHub().signInitialTransaction(txbWithCash);

       subFlow((FlowLogic)(new IdentitySyncFlow.Send(this.otherSideSession, ptx2.getTx())));
       subFlow((FlowLogic)(new SendTransactionFlow(this.otherSideSession, ptx2)));
       subFlow((FlowLogic)(new IdentitySyncFlow.Receive(this.otherSideSession)));

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

