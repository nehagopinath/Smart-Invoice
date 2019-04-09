package com.template.cordapp.flows;

import com.template.cordapp.state.Asset;
import kotlin.TypeCastException;
import kotlin.test.AssertionsKt;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionResolutionException;
import net.corda.core.transactions.SignedTransaction;
import net.corda.finance.Currencies;
import org.junit.Before;
import org.junit.Test;

public final class CreateAssetStateFlowTests extends AbstractAssetJunitFlowTests {

        @Test
        public final void create_Asset_on_ledger_successfully/* $FF was: create Asset on ledger successfully*/() throws TransactionResolutionException {
            SignedTransaction stx = this.createAsset(this.getLenderOfSecurity(), this.getCusip(), "US BOND", Currencies.DOLLARS(1000));
            this.getNetwork().waitQuiescent();
            ContractState var10000 = this.getLenderOfSecurity().getServices().loadState(stx.getTx().outRef(0).getRef()).getData();
            if (var10000 == null) {
                throw new TypeCastException("null cannot be cast to non-null type com.template.cordapp.state.Asset");
            } else {
                Asset asset = (Asset)var10000;
                AssertionsKt.assertEquals(asset.getCusip(), this.getCusip(),"expected equals actual");
            }
        }
    }

