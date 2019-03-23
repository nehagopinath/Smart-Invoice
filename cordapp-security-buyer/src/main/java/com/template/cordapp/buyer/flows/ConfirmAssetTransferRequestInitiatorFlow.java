package com.template.cordapp.buyer.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.template.cordapp.common.exception.InvalidPartyException;
import com.template.cordapp.common.flows.IdentitySyncFlow;
import com.template.cordapp.contract.AssetTransferContract;
import com.template.cordapp.flows.AbstractConfirmAssetTransferRequestFlow;
import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import com.template.cordapp.state.RequestStatus;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.confidential.SwapIdentitiesFlow;
import net.corda.core.contracts.*;
import net.corda.core.flows.CollectSignaturesFlow;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import static com.template.cordapp.state.RequestStatus.PENDING;


@StartableByRPC
public class ConfirmAssetTransferRequestInitiatorFlow extends AbstractConfirmAssetTransferRequestFlow {

   private final UniqueIdentifier linearId;
   private final Party clearingHouse;
   /**
    * The progress tracker provides checkpoints indicating the progress of the flow to observers.
    */
   private final ProgressTracker progressTracker = new ProgressTracker();

   public ConfirmAssetTransferRequestInitiatorFlow(UniqueIdentifier linearId, Party clearingHouse) {
      this.linearId = linearId;
      this.clearingHouse = clearingHouse;
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

      //Swap Identity
      LinkedHashMap txKeys = (LinkedHashMap) subFlow(new SwapIdentitiesFlow(clearingHouse));
      boolean size = txKeys.size() == 2;
      if (!size) {
         String illegalState = "Something went wrong when generating confidential identities.";
         throw new IllegalStateException(illegalState);
      }

      AnonymousParty anonymousCustodian = (AnonymousParty) txKeys.get(this.clearingHouse);
      if (anonymousCustodian == null) {
         throw new FlowException("Couldn't create clearing house anonymous identity.");
      }

      //initialising

      AnonymousParty anonymousMe = (AnonymousParty) txKeys.get(this.getOurIdentity());

      StateAndRef input = this.loadState(this.getServiceHub(), this.linearId, AssetTransfer.class);

      Collection participants1 = (Collection) ((AssetTransfer) input.getState().getData()).getParticipants();
      Intrinsics.checkExpressionValueIsNotNull(anonymousCustodian, "anonymousCustodian");
      List participants = CollectionsKt.plus(participants1, anonymousCustodian);

      Asset asset = (Asset) input.getState().getData();
      AssetTransfer assetTransfer = (AssetTransfer) input.getState().getData();
      AbstractParty abstractParty = (AbstractParty) anonymousCustodian;
      TransactionBuilder txb = null;
      RequestStatus requestStatus = PENDING;

      AssetTransfer output = new AssetTransfer(asset, null, anonymousMe, anonymousCustodian, PENDING, participants, linearId);


         if (getOurIdentity().getName() != this.resolveIdentity(this.getServiceHub(), output.getSecurityBuyer()).getName()) {
            throw new InvalidPartyException("Flow must be initiated by Lender Of Cash.");
         }

         PublicKey ourSigningKey = assetTransfer.getSecuritySeller().getOwningKey();

         final Command<AssetTransferContract.Commands.ConfirmRequest> command = new Command(
                 new AssetTransferContract.Commands.ConfirmRequest(),
                 ImmutableList.of(assetTransfer.getParticipants()));


         // We create a transaction builder and add the components.
         TransactionBuilder txBuilder = new TransactionBuilder(notary)
                 .addInputState(input)
                 .addOutputState(output, AssetTransferContract.ASSET_CONTRACT_ID)
                 .addCommand(command)
                 .setTimeWindow(getServiceHub().getClock().instant(), Duration.ofSeconds(60));

         // Signing the transaction.
         SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder, output.getSecurityBuyer().getOwningKey());

         //Todo: Get counter-party flow session

         // Creating a session with the other party.
         FlowSession otherPartySession = initiateFlow(clearingHouse);

         // Obtaining the counter-party's signature.
         final SignedTransaction fullySignedTx = (SignedTransaction) subFlow(
                 new CollectSignaturesFlow(signedTx, ImmutableSet.of(otherPartySession), CollectionsKt.listOf(output.getSecurityBuyer().getOwningKey()), CollectSignaturesFlow.Companion.tracker()));

         // Finalising the transaction.
         return (SignedTransaction) subFlow(new FinalityFlow(fullySignedTx));


      }
   }



