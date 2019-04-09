package com.template.cordapp.flows;

import com.template.cordapp.state.AssetTransfer;
import com.template.cordapp.state.RequestStatus;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin._Assertions;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionResolutionException;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.StartedMockNode;
import org.junit.Test;

import static com.template.cordapp.state.RequestStatus.*;

public final class ConfirmAssetTransferRequestFlowTests extends AbstractAssetJunitFlowTests {
        @Test
        public final void confirm_asset_transfer_request_successfully/* $FF was: confirm asset transfer request successfully*/() throws Throwable {
            this.createAsset();
            final SignedTransaction stx1 = this.createAssetTransferRequest(this.getLenderOfSecurity(), this.getLenderOfCashParty(), this.getCusip());
            this.getNetwork().waitQuiescent();
            final Ref.ObjectRef linearId = new Ref.ObjectRef();
            linearId.element = (UniqueIdentifier)null;
            this.getLenderOfCash().transaction((Function0)(new Function0() {
                // $FF: synthetic method
                // $FF: bridge method

                public final Object invoke() {
                    Ref.ObjectRef var10000 = linearId;
                    ContractState var10001 = null;
                    try {
                        var10001 = ConfirmAssetTransferRequestFlowTests.this.getLenderOfCash().getServices().loadState(stx1.getTx().outRef(0).getRef()).getData();
                    } catch (TransactionResolutionException e) {
                        e.printStackTrace();
                    }
                    if (var10001 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type com.synechron.cordapp.state.AssetTransfer");
                    } else {
                        return var10000.element = ((AssetTransfer)var10001).getLinearId();
                    }
                }
            }));
            StartedMockNode var10001 = this.getLenderOfCash();
            Party var10002 = this.getCustodianParty();
            UniqueIdentifier var10003 = (UniqueIdentifier)linearId.element;
            if (var10003 == null) {
                Intrinsics.throwNpe();
            }

            final SignedTransaction stx2 = this.confirmAssetTransferRequest(var10001, var10002, var10003);
            this.getNetwork().waitQuiescent();
            final Ref.ObjectRef assetTransfer = new Ref.ObjectRef();
            assetTransfer.element = (AssetTransfer)null;
            this.getGlobalCustodian().transaction((Function0)(new Function0() {
                // $FF: synthetic method
                // $FF: bridge method

                public final Object invoke() {
                    Ref.ObjectRef var10000 = assetTransfer;
                    ContractState var10001 = null;
                    try {
                        var10001 = ConfirmAssetTransferRequestFlowTests.this.getGlobalCustodian().getServices().loadState(stx2.getTx().outRef(0).getRef()).getData();
                    } catch (TransactionResolutionException e) {
                        e.printStackTrace();
                    }
                    if (var10001 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type com.synechron.cordapp.state.AssetTransfer");
                    } else {
                        var10000.element = (AssetTransfer) var10001;
                        AssetTransfer var3 = (AssetTransfer) assetTransfer.element;
                        if (var3 == null) {
                            Intrinsics.throwNpe();
                        }

                        boolean var1 = var3.getStatus().equals(PENDING);
                        if (_Assertions.ENABLED && !var1) {
                            String var2 = "Assertion failed";
                            try {
                                throw (Throwable) (new AssertionError(var2));
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                    return null;
                }
            }));
            var10001 = this.getGlobalCustodian();
            AssetTransfer var9 = (AssetTransfer)assetTransfer.element;
            if (var9 == null) {
                Intrinsics.throwNpe();
            }

            Party maybePartyBLookedUpByC = this.resolveIdentity(var10001, var9.getSecurityBuyer());
            var10001 = this.getGlobalCustodian();
            var9 = (AssetTransfer)assetTransfer.element;
            if (var9 == null) {
                Intrinsics.throwNpe();
            }

            Party maybePartyALookedUpByC = this.resolveIdentity(var10001, var9.getSecuritySeller());
            boolean var7 = Intrinsics.areEqual(this.getLenderOfSecurityParty(), maybePartyALookedUpByC);
            String var8;
            if (_Assertions.ENABLED && !var7) {
                var8 = "Assertion failed";
                throw (Throwable)(new AssertionError(var8));
            } else {
                var7 = Intrinsics.areEqual(this.getLenderOfCashParty(), maybePartyBLookedUpByC);
                if (_Assertions.ENABLED && !var7) {
                    var8 = "Assertion failed";
                    throw (Throwable)(new AssertionError(var8));
                }
            }
        }
    }




