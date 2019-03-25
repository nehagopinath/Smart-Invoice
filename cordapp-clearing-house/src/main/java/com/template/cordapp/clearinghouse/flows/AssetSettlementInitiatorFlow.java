package com.template.cordapp.clearinghouse.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.template.cordapp.common.exception.InvalidPartyException;
import com.template.cordapp.common.flows.ReceiveTransactionUnVerifiedFlow;
import com.template.cordapp.common.flows.IdentitySyncFlow.Send;
import com.template.cordapp.contract.AssetTransferContract;
import com.template.cordapp.contract.AssetTransferContract.Commands.SettleRequest;
import com.template.cordapp.flows.AbstractAssetSettlementFlow;
import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import com.template.cordapp.state.RequestStatus;
import java.security.PublicKey;
import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import net.corda.confidential.IdentitySyncFlow.Receive;
import net.corda.core.contracts.AttachmentConstraint;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.ServicesForResolution;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.KotlinUtilsKt;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import org.jetbrains.annotations.NotNull;
import static com.template.cordapp.state.RequestStatus.TRANSFERRED;


@StartableByRPC

public final class AssetSettlementInitiatorFlow extends AbstractAssetSettlementFlow {

   private final ProgressTracker progressTracker=new ProgressTracker();
   private final UniqueIdentifier linearId;


   @Override
   public ProgressTracker getProgressTracker() {
      return this.progressTracker;
   }


    public AssetSettlementInitiatorFlow(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    @Suspendable
   @NotNull
   public SignedTransaction call() throws FlowException{

       Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
       //initialization
       StateAndRef inAssetTransfer = this.loadState(this.getServiceHub(), this.linearId, AssetTransfer.class);
       List participants = (inAssetTransfer.getState().getData()).getParticipants();

       Asset asset = (Asset) inAssetTransfer.getState().getData();
       AssetTransfer assetTransfer = (AssetTransfer) inAssetTransfer.getState().getData();

       AssetTransfer outAssetTransfer = new AssetTransfer(asset, null, null, null, TRANSFERRED, participants, linearId);

       if (getOurIdentity().getName() != this.resolveIdentity(this.getServiceHub(), outAssetTransfer.getClearingHouse()).getName()) {
           throw new InvalidPartyException("Flow must be initiated by Custodian.");
       }

       final Command<AssetTransferContract.Commands.ConfirmRequest> command = new Command(
               new AssetTransferContract.Commands.ConfirmRequest(),
               ImmutableList.of(assetTransfer.getParticipants()));


       // build
       TransactionBuilder txBuilder = new TransactionBuilder(notary)
               .addInputState(inAssetTransfer)
               .addOutputState(outAssetTransfer, AssetTransferContract.ASSET_CONTRACT_ID)
               .addCommand(command)
               .setTimeWindow(getServiceHub().getClock().instant(), Duration.ofSeconds(60));

       //collect states

       //Create temporary partial transaction.
       SignedTransaction tempPtx = this.getServiceHub().signInitialTransaction(txBuilder);

       //Send partial transaction to Security Owner i.e. `Seller`.
       FlowSession securitySellerSession = this.initiateFlow(this.resolveIdentity(this.getServiceHub(), outAssetTransfer.getSecuritySeller()));
       this.subFlow(new SendTransactionFlow(securitySellerSession, tempPtx));

       //Receive transaction with input and output of Asset state.
       SignedTransaction assetPtx = (SignedTransaction) this.subFlow( (new ReceiveTransactionUnVerifiedFlow(securitySellerSession)));

       //Send partial transaction to `Buyer`.
       FlowSession securityBuyerSession = this.initiateFlow(this.resolveIdentity(this.getServiceHub(), outAssetTransfer.getSecurityBuyer()));
       this.subFlow((new SendTransactionFlow(securityBuyerSession, tempPtx)));

       //Send flows ID for soft lock of the cash state.
       securityBuyerSession.send(txBuilder.getLockId());

       //Receive and register the anonymous identity were created for Cash transfer.
       this.subFlow(((new Receive(securityBuyerSession)));
       //Receive transaction with input and output state of Cash state.
       SignedTransaction cashPtx = (SignedTransaction) this.subFlow((new ReceiveTransactionUnVerifiedFlow(securityBuyerSession)));
       //Add Asset states and commands to origin Transaction Builder `txb`.
       LedgerTransaction assetLtx = assetPtx.toLedgerTransaction(this.getServiceHub(), false);



       Iterable assetInputs = assetLtx.getInputs();
       Iterable assetOutputs = assetLtx.getInputs();
       Iterable assetCommands= assetLtx.getCommands();
       Iterator inputsIterator = assetInputs.iterator();
       Iterator outputsIterator = assetOutputs.iterator();
       Iterator commandsIterator = assetCommands.iterator();

       Object element;
       while(inputsIterator.hasNext()) {
           element = inputsIterator.next();
           StateAndRef it = (StateAndRef)element;
           txBuilder.addInputState(it);
       }

       while(outputsIterator.hasNext()) {
           element = outputsIterator.next();
           TransactionState it = (TransactionState)element;
           txBuilder.addOutputState(it);
       }

       while(commandsIterator.hasNext()) {
           element = commandsIterator.next();
           CommandWithParties it = (CommandWithParties)element;
           txBuilder.addCommand(new Command(it.getValue(), it.getSigners()));
       }


       LedgerTransaction cashLtx = cashPtx.toLedgerTransaction(this.getServiceHub(), false);

       Iterable cashInputs = cashLtx.getInputs();
       Iterable cashOutputs = cashLtx.getInputs();
       Iterable cashCommands= cashLtx.getCommands();
       Iterator inputsIteratorc = cashInputs.iterator();
       Iterator outputsIteratorc = cashOutputs.iterator();
       Iterator commandsIteratorc = cashCommands.iterator();

       while(inputsIteratorc.hasNext()) {
           element = inputsIteratorc.next();
           StateAndRef it = (StateAndRef)element;
           txBuilder.addInputState(it);
       }

       while(outputsIteratorc.hasNext()) {
           element = outputsIteratorc.next();
           TransactionState it = (TransactionState)element;
           txBuilder.addOutputState(it);
       }

       while(commandsIteratorc.hasNext()) {
           element = commandsIteratorc.next();
           CommandWithParties it = (CommandWithParties)element;
           txBuilder.addCommand(new Command(it.getValue(), it.getSigners()));
       }


         //identity sync
         Set counterPartySessions = SetsKt.setOf(new FlowSession[]{securityBuyerSession, securitySellerSession});
         this.subFlow((FlowLogic)(new Send(counterPartySessions, txBuilder.toWireTransaction((ServicesForResolution)this.getServiceHub()), AssetSettlementInitiatorFlow.Companion.IDENTITY_SYNC.INSTANCE.childProgressTracker())));


       // Signature
       SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder, outAssetTransfer.getSecurityBuyer().getOwningKey());

       //Todo: Get counter-party flow session

       // Creating a session with the other party.
       FlowSession otherPartySession = initiateFlow(clearingHouse);

       // Obtaining the counter-party's signature.
       final SignedTransaction fullySignedTx = (SignedTransaction) subFlow(
               new CollectSignaturesFlow(signedTx, ImmutableSet.of(otherPartySession), CollectionsKt.listOf(outAssetTransfer.getSecurityBuyer().getOwningKey()), CollectSignaturesFlow.Companion.tracker()));

       //finalize
       return (SignedTransaction) subFlow(new FinalityFlow(fullySignedTx));

   }
   }




