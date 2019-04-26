package com.template.cordapp.common.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.AttachmentResolutionException
import net.corda.core.contracts.TransactionResolutionException
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.internal.ResolveTransactionsFlow
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.unwrap

class ReceiveTransactionUnVerifiedFlow(private val otherSideSession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    @Throws(AttachmentResolutionException::class,
            TransactionResolutionException::class)
    override fun call(): SignedTransaction {
        val stx = otherSideSession.receive<SignedTransaction>().unwrap {
            subFlow(ResolveTransactionsFlow(it, otherSideSession))
            it
        }
        return stx
    }
}