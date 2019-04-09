package com.template.cordapp.flows;

import com.template.cordapp.state.AssetTransfer;
import kotlin.TypeCastException;
import kotlin.test.AssertionsKt;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionResolutionException;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.internal.InternalTestUtilsKt;
import org.junit.Test;


    public final class CreateAssetTransferRequestFlowTests extends AbstractAssetJunitFlowTests {
        @Test
        public final void create_asset_transfer_request_successfully/* $FF was: create asset transfer request successfully*/() throws TransactionResolutionException {
            Party lenderOfSecurityParty = InternalTestUtilsKt.chooseIdentity(this.getLenderOfSecurity().getInfo());
            Party lenderOfCashParty = InternalTestUtilsKt.chooseIdentity(this.getLenderOfCash().getInfo());
            this.createAsset();
            SignedTransaction stx = this.createAssetTransferRequest(this.getLenderOfSecurity(), lenderOfCashParty, this.getCusip());
            this.getNetwork().waitQuiescent();
            ContractState var10000 = this.getLenderOfCash().getServices().loadState(stx.getTx().outRef(0).getRef()).getData();

            if (var10000 == null) {
                throw new TypeCastException("null cannot be cast to non-null type com.synechron.cordapp.state.AssetTransfer");
            } else {
                AssetTransfer assetTransfer1 = (AssetTransfer)var10000;
                var10000 = this.getLenderOfSecurity().getServices().loadState(stx.getTx().outRef(0).getRef()).getData();
                if (var10000 == null) {
                    throw new TypeCastException("null cannot be cast to non-null type com.synechron.cordapp.state.AssetTransfer");
                } else {
                    AssetTransfer assetTransfer2 = (AssetTransfer)var10000;
                    AssertionsKt.assertEquals(assetTransfer1, assetTransfer2, "expected equals actual");
                    Party maybePartyALookedUpByA = this.resolveIdentity(this.getLenderOfSecurity(), assetTransfer1.getSecuritySeller());
                    Party maybePartyALookedUpByB = this.resolveIdentity(this.getLenderOfSecurity(), assetTransfer1.getSecurityBuyer());
                    AssertionsKt.assertEquals(lenderOfSecurityParty, maybePartyALookedUpByA,"expected equals actual");
                    AssertionsKt.assertEquals(lenderOfCashParty, maybePartyALookedUpByB,"expected equals actual");
                    Party maybePartyBLookedUpByA = this.resolveIdentity(this.getLenderOfCash(), assetTransfer1.getSecurityBuyer());
                    Party maybePartyBLookedUpByB = this.resolveIdentity(this.getLenderOfCash(), assetTransfer1.getSecuritySeller());
                    AssertionsKt.assertEquals(lenderOfCashParty, maybePartyBLookedUpByA,"expected equals actual");
                    AssertionsKt.assertEquals(lenderOfSecurityParty, maybePartyBLookedUpByB,"expected equals actual");
                }
            }
        }
    }



