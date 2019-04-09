package com.template.cordapp.flows;

import com.template.cordapp.state.Asset;
import com.template.cordapp.state.AssetTransfer;
import com.template.cordapp.state.RequestStatus;
import kotlin.Unit;
import kotlin._Assertions;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.transactions.SignedTransaction;
import net.corda.finance.Currencies;
import net.corda.finance.contracts.GetBalances;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

    public final class AssetSettlementFlowTests extends AbstractAssetJunitFlowTests {
        @Test
        public final void process_asset_transfer_settlement/* $FF was: process asset transfer settlement*/() throws Throwable {
            this.createAsset();
            SignedTransaction stx1 = this.createAssetTransferRequest(this.getLenderOfSecurity(), this.getLenderOfCashParty(), this.getCusip());
            this.getNetwork().waitQuiescent();
            final AssetTransfer assetTransfer = (AssetTransfer)this.getLenderOfSecurity().transaction((Function0)(new Function0() {
                // $FF: synthetic method
                // $FF: bridge method

                @NotNull
                public final AssetTransfer invoke() {
                    List states = com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfSecurity().getServices().getVaultService().queryBy(Asset.class).getStates();
                    boolean var2 = states.size() == 1;
                    if (_Assertions.ENABLED && !var2) {
                        String var3 = "Assertion failed";
                        try {
                            throw (Throwable)(new AssertionError(var3));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    } else {
                        List states2 = com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfSecurity().getServices().getVaultService().queryBy(AssetTransfer.class).getStates();
                        return (AssetTransfer)((StateAndRef) CollectionsKt.single(states2)).getState().getData();
                    }

                    return null;
                }
            }));
            this.confirmAssetTransferRequest(this.getLenderOfCash(), this.getCustodianParty(), assetTransfer.getLinearId());
            this.getNetwork().waitQuiescent();
            this.selfIssueCash(this.getLenderOfCash(), Currencies.DOLLARS(2000));
            this.getLenderOfCash().transaction((Function0)(new Function0() {
                // $FF: synthetic method
                // $FF: bridge method

                public final Object invoke() {
                    boolean var1 = Intrinsics.areEqual((Amount) MapsKt.getValue(GetBalances.getCashBalances(com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfCash().getServices()), Currencies.USD), Currencies.DOLLARS(2000));
                    if (_Assertions.ENABLED && !var1) {
                        String var2 = "Assertion failed";
                        try {
                            throw (Throwable)(new AssertionError(var2));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    return null;
                }
            }));
            this.getLenderOfSecurity().transaction((Function0)(new Function0() {
                // $FF: synthetic method
                // $FF: bridge method
                public Object invoke() {
                    this.invoke();
                    return Unit.INSTANCE;
                }

              /*  public final Object invoke() {
                    boolean var1 = GetBalances.getCashBalances(com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfSecurity().getServices()).isEmpty();
                    if (_Assertions.ENABLED && !var1) {
                        String var2 = "Assertion failed";
                        try {
                            throw (Throwable)(new AssertionError(var2));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    return null;
                } */
            }));
            this.settleAssetTransferRequest(this.getGlobalCustodian(), assetTransfer.getLinearId());
            this.getNetwork().waitQuiescent();
            this.getLenderOfSecurity().transaction((Function0)(new Function0() {
                // $FF: synthetic method
                // $FF: bridge method

                public final Object invoke() {
                    boolean var1 = com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfSecurity().getServices().getVaultService().queryBy(Asset.class).getStates().isEmpty();
                    if (_Assertions.ENABLED && !var1) {
                        String var5 = "Assertion failed";
                        try {
                            throw (Throwable)(new AssertionError(var5));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    } else {
                        AssetTransfer assetTransfer2 = (AssetTransfer)((StateAndRef)CollectionsKt.first(com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfSecurity().getServices().getVaultService().queryBy(AssetTransfer.class).getStates())).getState().getData();
                        boolean var2 = assetTransfer2.getStatus().equals(RequestStatus.TRANSFERRED);
                        String var3;
                        if (_Assertions.ENABLED && !var2) {
                            var3 = "Assertion failed";
                            try {
                                throw (Throwable)(new AssertionError(var3));
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } else {
                            var2 = Intrinsics.areEqual(assetTransfer2.getAsset().getPurchaseCost(), (Amount)MapsKt.getValue(GetBalances.getCashBalances(com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfSecurity().getServices()), Currencies.USD));
                            if (_Assertions.ENABLED && !var2) {
                                var3 = "Assertion failed";
                                try {
                                    throw (Throwable)(new AssertionError(var3));
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        }
                    }
                    return null;
                }
            }));
            this.getLenderOfCash().transaction((Function0)(new Function0() {
                // $FF: synthetic method
                // $FF: bridge method

                public final Object invoke() {
                    boolean var1 = Intrinsics.areEqual((Amount)MapsKt.getValue(GetBalances.getCashBalances(com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfCash().getServices()), Currencies.USD), Currencies.DOLLARS(1000));
                    if (_Assertions.ENABLED && !var1) {
                        String var7 = "Assertion failed";
                        try {
                            throw (Throwable)(new AssertionError(var7));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    } else {
                        AssetTransfer assetTransfer3 = (AssetTransfer)((StateAndRef)CollectionsKt.first(com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfCash().getServices().getVaultService().queryBy(AssetTransfer.class).getStates())).getState().getData();
                        boolean var2 = assetTransfer3.getStatus().equals(RequestStatus.TRANSFERRED);
                        if (_Assertions.ENABLED && !var2) {
                            String var8 = "Assertion failed";
                            try {
                                throw (Throwable)(new AssertionError(var8));
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } else {
                            List assetStates = com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfCash().getServices().getVaultService().queryBy(Asset.class).getStates();
                            boolean var3 = assetStates.size() == 1;
                            String var4;
                            if (_Assertions.ENABLED && !var3) {
                                var4 = "Assertion failed";
                                try {
                                    throw (Throwable)(new AssertionError(var4));
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            } else {
                                var3 = Intrinsics.areEqual(com.template.cordapp.flows.AssetSettlementFlowTests.this.resolveIdentity(com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfCash(), ((Asset)((StateAndRef)CollectionsKt.first(assetStates)).getState().getData()).getOwner()).getName(), com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfCashParty().getName());
                                if (_Assertions.ENABLED && !var3) {
                                    var4 = "Assertion failed";
                                    try {
                                        throw (Throwable)(new AssertionError(var4));
                                    } catch (Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                } else {
                                    var3 = Intrinsics.areEqual(((Asset)((StateAndRef)CollectionsKt.first(assetStates)).getState().getData()).getOwner(), assetTransfer.getSecurityBuyer());
                                    if (_Assertions.ENABLED && !var3) {
                                        var4 = "Assertion failed";
                                        try {
                                            throw (Throwable)(new AssertionError(var4));
                                        } catch (Throwable throwable) {
                                            throwable.printStackTrace();
                                        }
                                    } else {
                                        var3 = Intrinsics.areEqual((Asset)((StateAndRef)CollectionsKt.first(assetStates)).getState().getData(), Asset.copy$default(assetTransfer.getAsset(), (String)null, (String)null, (Amount)null, assetTransfer.getSecurityBuyer(), 7, (Object)null));
                                        if (_Assertions.ENABLED && !var3) {
                                            var4 = "Assertion failed";
                                            try {
                                                throw (Throwable)(new AssertionError(var4));
                                            } catch (Throwable throwable) {
                                                throwable.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return null;
                }
            }));
            this.getGlobalCustodian().transaction((Function0)(new Function0() {
                // $FF: synthetic method
                // $FF: bridge method

                public final Object invoke() {
                    AssetTransfer assetTransfer4 = (AssetTransfer)((StateAndRef)CollectionsKt.first(com.template.cordapp.flows.AssetSettlementFlowTests.this.getLenderOfCash().getServices().getVaultService().queryBy(AssetTransfer.class).getStates())).getState().getData();
                    boolean var2 = assetTransfer4.getStatus().equals(RequestStatus.TRANSFERRED);
                    if (_Assertions.ENABLED && !var2) {
                        String var3 = "Assertion failed";
                        try {
                            throw (Throwable)(new AssertionError(var3));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    return null;
                }
            }));
        }
    }


