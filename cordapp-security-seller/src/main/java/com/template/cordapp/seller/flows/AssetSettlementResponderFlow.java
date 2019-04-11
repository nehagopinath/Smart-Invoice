package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.cordapp.common.exception.TooManyStatesFoundException;
import com.template.cordapp.common.flows.IdentitySyncFlowReceive;
import com.template.cordapp.common.flows.SignTxFlow;
import com.template.cordapp.contract.AssetContract;
import com.template.cordapp.contract.AssetTransferContract;
import com.template.cordapp.flows.AbstractAssetSettlementFlow;
import com.template.cordapp.flows.FlowLogicCommonMethods;
import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;

import com.template.cordapp.utils.Utils;
import kotlin.collections.CollectionsKt;
import net.corda.confidential.IdentitySyncFlow;
import net.corda.core.contracts.*;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.StatesToRecord;
import net.corda.core.schemas.QueryableState;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Signed;
import java.security.SignatureException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Seller review the received settlement transaction then create and send new temporary transaction
 * to send input, output [Asset] states and command to change ownership to `Buyer` party.
 */

//todo 5: resolve the circular Dependency
@InitiatedBy(AbstractAssetSettlementFlow.class)

public final class AssetSettlementResponderFlow extends FlowLogic<SignedTransaction> implements FlowLogicCommonMethods {

   private final FlowSession otherSideSession;

   private final ProgressTracker.Step ADD_ASSET = new ProgressTracker.Step("Performing initial steps");
   private final ProgressTracker.Step SYNC_IDENTITY  = new ProgressTracker.Step("Building and verifying transaction");


   /**
    * The progress tracker provides checkpoints indicating the progress of the flow to observers.
    */

   private final ProgressTracker progressTracker = new ProgressTracker(
           ADD_ASSET,
           SYNC_IDENTITY
   );

   @Override
   public ProgressTracker getProgressTracker() {
      return progressTracker;
   }

   public AssetSettlementResponderFlow(FlowSession otherSideSession) {
      this.otherSideSession = otherSideSession;
   }

   @Suspendable
   public SignedTransaction call() throws FlowException {

      progressTracker.setCurrentStep(ADD_ASSET);
      Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);


      SignedTransaction ptx = subFlow(new ReceiveTransactionFlow(otherSideSession, false, StatesToRecord.NONE));

      try {
         LedgerTransaction ltx1 = ptx.toLedgerTransaction(getServiceHub(), false);
      } catch (SignatureException e) {
         e.printStackTrace();
      }

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

      //todo 6: Check this exception
      if (assetTransfer != null){
         throw (new TooManyStatesFoundException("Transaction with more than one `AssetTransfer` " + "input states received from `" + this.otherSideSession.getCounterparty() + "` party"));
      }

      StateAndRef assetStateAndRef = (StateAndRef) Utils.getAssetByCusip(getServiceHub(),assetTransfer.getAsset().getCusip());

      //todo 7: Check this below line for the function withNewOwner()

      //CommandAndState(cmd, assetOutState)commandAndState = (CommandAndState)assetStateAndRef.getState().getData().withNewOwner(assetTransfer.getSecurityBuyer());


      TransactionBuilder txBuilder = new TransactionBuilder(notary)
              .addInputState(assetStateAndRef);
              //.addOutputState(assetOutState, AssetContract.ASSET_CONTRACT_ID)
             // .addCommand(cmd, assetOutState.getOwner.getOwningKey);


      SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

      progressTracker.setCurrentStep(SYNC_IDENTITY);
      this.subFlow((new SendTransactionFlow(otherSideSession, signedTx)));

      subFlow(new IdentitySyncFlow.Receive(otherSideSession));

      SignedTransaction stx = subFlow(new SignTxFlow(otherSideSession));

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

