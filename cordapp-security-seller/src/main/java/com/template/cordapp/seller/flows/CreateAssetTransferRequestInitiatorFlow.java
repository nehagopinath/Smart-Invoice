package com.template.cordapp.seller.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.template.cordapp.common.exception.InvalidPartyException;
import com.template.cordapp.utils.Utils;
import com.template.cordapp.contract.AssetTransferContract;
import com.template.cordapp.flows.AbstractCreateAssetTransferRequestFlow;
import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import java.security.PublicKey;
import java.time.Duration;
import java.util.*;
import kotlin.collections.CollectionsKt;
import net.corda.confidential.SwapIdentitiesFlow;
import net.corda.core.contracts.*;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import static com.template.cordapp.state.RequestStatus.PENDING_CONFIRMATION;

/**
 * Owner of security (i.e. seller) creates [AssetTransfer] request state in-order to start deal with buyer.
 */

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class CreateAssetTransferRequestInitiatorFlow extends AbstractCreateAssetTransferRequestFlow<SignedTransaction> {

   private final String cusip;
   private final Party securityBuyer;

   /**
    * The progress tracker provides checkpoints indicating the progress of the flow to observers.
    */
   private final ProgressTracker progressTracker = new ProgressTracker();

   public CreateAssetTransferRequestInitiatorFlow(String cusip, Party securityBuyer) {
      this.cusip = cusip;
      this.securityBuyer = securityBuyer;
   }

   @Override
   public ProgressTracker getProgressTracker() {
      return progressTracker;
   }

   /**
    * The flow logic is encapsulated within the call() method.
    */
   @Suspendable
   @Override
   public SignedTransaction call() throws FlowException {
      // We retrieve the notary identity from the network map.
      Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

      if (getOurIdentity().getName() == securityBuyer.getName()) throw new InvalidPartyException("Flow initiating party should not equal to Lender of Cash party.");

      //initialising
      LinkedHashMap txKeys = subFlow(new SwapIdentitiesFlow(securityBuyer));
      boolean size = txKeys.size() == 2;
      if(!size)
      {
         String illegalState = "Something went wrong when generating confidential identities.";
         throw new IllegalStateException(illegalState);
      }

      AnonymousParty anonymousMe = (AnonymousParty)txKeys.get(this.getOurIdentity());
      if (anonymousMe == null) {
         throw new FlowException("Couldn't create our anonymous identity.");
      }

      AnonymousParty anonymousCashLender = (AnonymousParty)txKeys.get(this.securityBuyer);
      if (anonymousCashLender == null) {
         throw new FlowException("Couldn't create lender's (securityBuyer) anonymous identity.");
      }

      // We create the transaction components.
      Asset asset = (Asset) Utils.getAssetByCusip(this.getServiceHub(), this.cusip).getState().getData();

      AssetTransfer assetTransfer = new AssetTransfer(asset, anonymousMe, anonymousCashLender, null, PENDING_CONFIRMATION,null,null);

      PublicKey ourSigningKey = assetTransfer.getSecuritySeller().getOwningKey();

      final Command<AssetTransferContract.Commands.CreateRequest> command = new Command(
              new AssetTransferContract.Commands.CreateRequest(),
              ImmutableList.of(assetTransfer.getParticipants()));


      // We create a transaction builder and add the components.
      TransactionBuilder txBuilder = new TransactionBuilder(notary)
              .addOutputState(assetTransfer, AssetTransferContract.ASSET_CONTRACT_ID)
              .addCommand(command)
              .setTimeWindow(getServiceHub().getClock().instant(), Duration.ofSeconds(30));

      // Signing the transaction.
      SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder,ourSigningKey);

      // Creating a session with the other party.
      FlowSession otherPartySession = initiateFlow(securityBuyer);

      // Obtaining the counter-party's signature.
      final SignedTransaction fullySignedTx = subFlow(
              new CollectSignaturesFlow(signedTx, ImmutableSet.of(otherPartySession), CollectionsKt.listOf(ourSigningKey),CollectSignaturesFlow.Companion.tracker()));

      // Finalising the transaction.
      return subFlow(new FinalityFlow(fullySignedTx));


   }
}

