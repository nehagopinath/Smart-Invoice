package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.exception.TooManyStatesFoundException;
import com.template.cordapp.common.flows.IdentitySyncFlow;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.flows.AbstractAssetSettlementFlow;
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


/**
 * Buyer review the received settlement transaction then issue the cash to `Seller` party.
 */

@InitiatedBy(AbstractAssetSettlementFlow.class)

public final class AssetSettlementResponderFlow extends FlowLogic<SignedTransaction> {

   private final FlowSession otherSideSession;

   public AssetSettlementResponderFlow(FlowSession otherSideSession) {

      this.otherSideSession = otherSideSession;
   }

    private final ProgressTracker.Step ADD_CASH = new ProgressTracker.Step("Add cash states");

    private final ProgressTracker.Step SYNC_IDENTITY = new ProgressTracker.Step("SYNC_IDENTITY");

    private final ProgressTracker progressTracker = new ProgressTracker(
            ADD_CASH,
            SYNC_IDENTITY
    );

   @Suspendable
   @Override
   public SignedTransaction call() throws FlowException {

       progressTracker.setCurrentStep(ADD_CASH);

       SignedTransaction ptx1 = (SignedTransaction) this.subFlow((FlowLogic) (new ReceiveTransactionFlow(this.otherSideSession, false, StatesToRecord.NONE)));

       LedgerTransaction ltx1 = null;
       try {
           ltx1 = ptx1.toLedgerTransaction(this.getServiceHub(), false);
       } catch (SignatureException e) {
           e.printStackTrace();
       }

       Iterable inputState= ltx1.getInputStates();
       Collection destinationAT = (new ArrayList());
       Iterator instIterator = inputState.iterator();

       while (instIterator.hasNext()) {
           Object o = instIterator.next();
           if (o instanceof AssetTransfer) {
               destinationAT.add(o);
           }
       }

       //throw too many states found exception if this fails
       AssetTransfer assetTransfer = (AssetTransfer) CollectionsKt.singleOrNull((List) destinationAT);

       /*if (assetTransfer != null){
           throw (new TooManyStatesFoundException("Transaction with more than one `AssetTransfer` " + "input states received from `" + this.otherSideSession.getCounterparty() + "` party"));
       } */

       FlowSession flowSession = this.otherSideSession;
       UntrustworthyData<UUID> receiver = flowSession.receive(UUID.class);
       UUID it = receiver.getFromUntrustedWorld();

       //Issue cash to security owner i.e. `Seller` party.

       Pair AB = Cash.generateSpend(this.getServiceHub(),
               new TransactionBuilder(ltx1.getNotary(),it,null,null,null,null,null,null), //soft reserve the cash state.
               assetTransfer.getAsset().getPurchaseCost(),
               this.getOurIdentityAndCert(),
               assetTransfer.getSecuritySeller(),
               null);

       TransactionBuilder txbWithCash = (TransactionBuilder) AB.component1();
       List cashSignKeys = (List)AB.component2();

       SignedTransaction ptx2 = this.getServiceHub().signInitialTransaction(txbWithCash);

       subFlow(new net.corda.confidential.IdentitySyncFlow.Send(this.otherSideSession, ptx2.getTx()));
       subFlow((FlowLogic)(new SendTransactionFlow(this.otherSideSession, ptx2)));

       progressTracker.setCurrentStep(SYNC_IDENTITY);
       subFlow((FlowLogic)(new IdentitySyncFlow.Receive(this.otherSideSession)));

       SignedTransaction stx = subFlow(new SignTxFlow(this.otherSideSession));

       return waitForLedgerCommit(stx.getId());


   }


}

