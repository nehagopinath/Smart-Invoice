package com.template.cordapp.server

import com.template.cordapp.state.Asset
import com.template.cordapp.state.AssetTransfer
import com.template.cordapp.seller.flows.CreateAssetStateFlow.Initiator
import com.template.cordapp.seller.flows.CreateAssetTransferRequestInitiatorFlow
import com.template.cordapp.buyer.flows.ConfirmAssetTransferRequestInitiatorFlow
import com.template.cordapp.clearinghouse.flows.AssetSettlementInitiatorFlow
import net.corda.core.utilities.OpaqueBytes
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.StateAndRef
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.utilities.getOrThrow
import net.corda.finance.AMOUNT
import net.corda.core.contracts.Amount
import net.corda.finance.contracts.asset.Cash.State
import net.corda.core.identity.Party
import net.corda.core.internal.declaredField
import net.corda.core.serialization.serialize
import net.corda.finance.USD
import net.corda.finance.flows.AbstractCashFlow
import net.corda.finance.flows.CashIssueFlow
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

val SERVICE_NAMES = listOf("Notary", "Network Map Service")

/**
 *  A Spring Boot Server API controller for interacting with the node via RPC.
 */

@RestController
@RequestMapping("/api/example/") // The paths for GET and POST requests are relative to this base path.
class MainController(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val myLegalName = rpc.proxy.nodeInfo().legalIdentities.first().name
    private val proxy = rpc.proxy

    /**
     * Returns the node's name.
     */
    @GetMapping(value = ["me"] , produces = [APPLICATION_JSON_VALUE] )
    fun whoami() = mapOf("me" to myLegalName)

    /**
     * Returns all parties registered with the network map service. These names can be used to look up identities using
     * the identity service.
     */
    @GetMapping(value = [ "peers" ], produces = [ APPLICATION_JSON_VALUE ])
    fun getPeers(): Map<String, List<CordaX500Name>> {
        val nodeInfo = proxy.networkMapSnapshot()
        return mapOf("peers" to nodeInfo
                .map { it.legalIdentities.first().name }
                //filter out myself, notary and eventual network map started by driver
                .filter { it.organisation !in (SERVICE_NAMES + myLegalName.organisation) })
    }

    /**
     * Displays all transactions that exist in the node's vault.
     */
    @GetMapping(value = [ "transactions" ], produces = [APPLICATION_JSON_VALUE])
    fun getTransactions() : ResponseEntity<List<StateAndRef<Asset>>> {
        return ResponseEntity.ok(proxy.vaultQueryBy<Asset>().states)
    }

    /**
     * Displays all transfers that exist in the node's vault.
     */

    @GetMapping(value = [ "transfers" ], produces = [APPLICATION_JSON_VALUE])
    fun getTransfers() : ResponseEntity<List<StateAndRef<AssetTransfer>>> {
        return ResponseEntity.ok(proxy.vaultQueryBy<AssetTransfer>().states)
    }

    /**
     * Displays cash statements that belong to node
     */

    @GetMapping(value = [ "cash" ], produces = [APPLICATION_JSON_VALUE])
    fun getCash() : ResponseEntity<List<StateAndRef<State>>> {
        return ResponseEntity.ok(proxy.vaultQueryBy<State>().states)
    }

    /**
     * Initiates a flow to create a transaction on Seller side.
     *
     * After a successful completion message is sent to Seller about status of flow.
     *
     */

    @PostMapping(value = [ "create-transaction" ], produces = [ TEXT_PLAIN_VALUE ], headers =  ["Content-Type=application/x-www-form-urlencoded"] )
    fun createTransaction(request: HttpServletRequest): ResponseEntity<String> {

        val cusip = request.getParameter("cusipValue")
        val assetName  = request.getParameter("transactionAssetName")
        val purchaseCost = request.getParameter("transactionPurchaseCost").toInt()


        if(cusip == null){
        return ResponseEntity.badRequest().body("Query parameter 'cusip' must not be null.\n")
        }
        if(assetName == null){
            return ResponseEntity.badRequest().body("Query parameter 'assetName' must not be null.\n")
        }
        if (purchaseCost <= 0 ) {
            return ResponseEntity.badRequest().body("Query parameter 'purchaseCost' must be non-negative.\n")
        }

        return try {
            val signedTx = proxy.startTrackedFlow(::Initiator,cusip, assetName, AMOUNT(purchaseCost,USD)).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Invoice id ${signedTx.id} was successfully created!.\n")

        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }

    /**
     * Initiates a flow to create a transfer on Seller side and send it to Buyer.
     *
     * After a successful completion message is sent to Seller about status of flow. Transfer appears on both
     * Buyer and Seller side in the status 'Pending Confirmation'
     *
     */

    @PostMapping(value = [ "create-transfer" ], produces = [ TEXT_PLAIN_VALUE ], headers =  ["Content-Type=application/x-www-form-urlencoded"] )
    fun createTransfer(request: HttpServletRequest): ResponseEntity<String> {

        val cusipTr = request.getParameter("cusipTr")
        val securityBuyer = request.getParameter("transferBuyer")


        if(cusipTr == null){
            return ResponseEntity.badRequest().body("Query parameter 'cusipTr' must not be null.\n")
        }
        if(securityBuyer == null){
            return ResponseEntity.badRequest().body("Query parameter 'securityBuyer' must not be null.\n")
        }

        val secBuyerName=CordaX500Name.parse(securityBuyer)
        val otherParty = proxy.wellKnownPartyFromX500Name(secBuyerName) ?: return ResponseEntity.badRequest().body("Party named $secBuyerName cannot be found.\n")

        return try {
            val signedTr = proxy.startTrackedFlow(::CreateAssetTransferRequestInitiatorFlow,cusipTr,otherParty).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Transfer id ${signedTr.id} was successfully sent to buyer!.\n")

        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }

    /**
     * Initiates a flow to confirm a transaction from buyer side.
     *
     * After a successful completion message is sent to Buyer about status of flow and status is updated to 'Pending'
     *
     */

    @PostMapping(value = [ "create-confirm" ], produces = [ TEXT_PLAIN_VALUE ], headers =  ["Content-Type=application/x-www-form-urlencoded"] )
    fun createConfirm(request: HttpServletRequest): ResponseEntity<String> {

        val linearId = request.getParameter("linearId")
        val clearingHouse = request.getParameter("clearingHouse")

        val linId = UniqueIdentifier.fromString(linearId)
        if(linearId == null){
            return ResponseEntity.badRequest().body("Query parameter 'linearId' must not be null.\n")
        }
        if(clearingHouse == null){
            return ResponseEntity.badRequest().body("Query parameter 'clearingHouse' must not be null.\n")
        }

        val clearingHouseName=CordaX500Name.parse(clearingHouse)
        val cleHouse = proxy.wellKnownPartyFromX500Name(clearingHouseName) ?: return ResponseEntity.badRequest().body("Party named $clearingHouseName cannot be found.\n")

        return try {
            val confirmedTr = proxy.startTrackedFlow(::ConfirmAssetTransferRequestInitiatorFlow,linId,cleHouse).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Transfer id ${confirmedTr.id} is conirmed by Buyer. Waiting for Clearing house verifcation\n")
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }

    }

    /**
     * Initiates a flow to validate transaction from Clearing house.
     *
     * After a successful completion message is sent to Seller about status of flow
     *
     */

    @PostMapping(value = [ "create-clear" ], produces = [ TEXT_PLAIN_VALUE ], headers =  ["Content-Type=application/x-www-form-urlencoded"] )
    fun createClear(request: HttpServletRequest): ResponseEntity<String> {

        val linearId = request.getParameter("linearId")


        val linrId = UniqueIdentifier.fromString(linearId)
        if (linearId == null) {
            return ResponseEntity.badRequest().body("Query parameter 'linearId' must not be null.\n")
        }

        return try {
            val clearTr = proxy.startTrackedFlow(::AssetSettlementInitiatorFlow, linrId).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Verification of ${clearTr.id} is successfully COMPLETED!\n")
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }

    /**
     * Initiates a flow to self issue cash on the buyer side.
     *
     *
     */

    @PostMapping( value = [ "create-issue" ], produces = [ TEXT_PLAIN_VALUE ], headers =  ["Content-Type=application/x-www-form-urlencoded"])
    fun createIssue(request: HttpServletRequest): ResponseEntity<String> {

        val amount = request.getParameter("amount")
        val issuerBank = request.getParameter("issuerBank").toByte()
        val notary = request.getParameter("notary")


        if(amount == null){
            return ResponseEntity.badRequest().body("Query parameter 'amount' must not be null.\n")
        }
        if(issuerBank <0){
            return ResponseEntity.badRequest().body("Query parameter 'issuerBank' must not be null.\n")
        }
        if(notary == null){
            return ResponseEntity.badRequest().body("Query parameter 'notary' must not be null.\n")
        }
        val am=Amount.parseCurrency(amount)

        val isBank = OpaqueBytes.of(issuerBank)

        val notaryName=CordaX500Name.parse(notary)
        val notaryIdent = proxy.wellKnownPartyFromX500Name(notaryName) ?: return ResponseEntity.badRequest().body("Party named $notaryName cannot be found.\n")

        return try {
            val cashTx = proxy.startTrackedFlow(::CashIssueFlow,am,isBank,notaryIdent).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Money was successfully issued")

        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message!!)
        }
    }

}