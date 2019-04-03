package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.clearinghouse.flows.AssetSettlementInitiatorFlow;
import com.template.cordapp.common.exception.TooManyStatesFoundException;
import com.template.cordapp.common.flows.IdentitySyncFlow;
import com.template.cordapp.common.flows.IdentitySyncFlowReceive;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.flows.FlowLogicCommonMethods;
import com.template.cordapp.state.AssetTransfer;

import kotlin.collections.CollectionsKt;
import net.corda.core.contracts.*;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.StatesToRecord;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@InitiatedBy(AssetSettlementInitiatorFlow.class)

public final class AssetSettlementResponderFlow extends FlowLogic<SignedTransaction> implements FlowLogicCommonMethods {
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
   public SignedTransaction call() throws FlowException {
      SignedTransaction ptx1 = (SignedTransaction) this.subFlow((FlowLogic) (new ReceiveTransactionFlow(this.otherSideSession, false, StatesToRecord.NONE)));

      //TODO check : not sure if this works
      LedgerTransaction ltx1 = null;
      try {
         ltx1 = ptx1.toLedgerTransaction(this.getServiceHub(), false);
      } catch (SignatureException e) {
         e.printStackTrace();
      }

      Iterable inputstates = (Iterable) ltx1.getInputStates();
      Collection destinationAT = (Collection) (new ArrayList());
      Iterator instIterator = inputstates.iterator();

      while (instIterator.hasNext()) {
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
      // TODO cordapp-common/utils should be fixed to fix this code   **
      // StateAndRef assetStateAndRef = t

      TransactionBuilder txb = new TransactionBuilder(ltx1.getNotary());
      /*
      **
      txb.addInputState(assetStateAndRef)
        txb.addOutputState(assetOutState, AssetContract.ASSET_CONTRACT_ID)
        txb.addCommand(Command(cmd, assetOutState.owner.owningKey))
       */

      SignedTransaction ptx2 = this.getServiceHub().signInitialTransaction(txb);
      this.subFlow((FlowLogic) (new SendTransactionFlow(this.otherSideSession, ptx2)));
      this.subFlow((FlowLogic) (new IdentitySyncFlowReceive(this.otherSideSession)));
      SignedTransaction stx = (SignedTransaction) this.subFlow((FlowLogic) (new SignTxFlow(this.otherSideSession)));

      return waitForLedgerCommit(stx.getId());
   }

   @Override
   public Party firstNotary(@NotNull ServiceHub $receiver) {
      return null;
   }

   @Override
   public StateAndRef loadState(@NotNull ServiceHub $receiver, @NotNull UniqueIdentifier linearId, @NotNull Class clazz) {
      return null;
   }

   @Override
   public Party resolveIdentity(@NotNull ServiceHub $receiver, @NotNull AbstractParty abstractParty) {
      return null;
   }
}

