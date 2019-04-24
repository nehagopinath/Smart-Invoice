package com.template.cordapp.state

import com.template.cordapp.schema.AssetTransferSchemaV1
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState

/**
 * This state acting as deal data before the actual [Asset] being transfer to target buyer party on settlement.
 */
data class AssetTransfer(val asset: Asset,
                         val securitySeller: AbstractParty,
                         val securityBuyer: AbstractParty,
                         val clearingHouse: AbstractParty?,
                         val status: RequestStatus,
                         override val participants: List<AbstractParty> = listOf(securityBuyer, securitySeller),
                         override val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState, QueryableState {
    override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is AssetTransferSchemaV1 -> AssetTransferSchemaV1.PersistentAssetTransfer(
                    cusip = this.asset.cusip,
                    securitySeller = this.securitySeller,
                    securityBuyer = this.securityBuyer,
                    clearingHouse = this.clearingHouse,
                    status = this.status.value,
                    participants = this.participants.toMutableSet(),
                    linearId = this.linearId.toString()
            )
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = setOf(AssetTransferSchemaV1)
}

