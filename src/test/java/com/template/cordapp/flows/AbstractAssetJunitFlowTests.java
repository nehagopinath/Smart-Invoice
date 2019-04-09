package com.template.cordapp.flows;

import com.google.common.collect.ImmutableList;
import com.template.cordapp.buyer.flows.AssetSettlementResponderFlow;
import com.template.cordapp.buyer.flows.ConfirmAssetTransferRequestInitiatorFlow;
import com.template.cordapp.buyer.flows.CreateAssetTransferRequestResponderFlow;
import com.template.cordapp.clearinghouse.flows.AssetSettlementInitiatorFlow;
import com.template.cordapp.clearinghouse.flows.ConfirmAssetTransferRequestResponderFlow;
import com.template.cordapp.exception.StateNotFoundOnVaultException;
import com.template.cordapp.seller.flows.ConfirmAssetTransferRequestHandlerFlow;
import com.template.cordapp.seller.flows.CreateAssetStateFlow;
import com.template.cordapp.seller.flows.CreateAssetTransferRequestInitiatorFlow;
import com.template.cordapp.state.Asset;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.test.AssertionsKt;
import net.corda.core.contracts.*;
import net.corda.core.flows.FlowLogic;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.NetworkParameters;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.KotlinUtilsKt;
import net.corda.core.utilities.OpaqueBytes;
import net.corda.finance.Currencies;
import net.corda.finance.flows.AbstractCashFlow;
import net.corda.finance.flows.CashIssueFlow;
import net.corda.testing.internal.InternalTestUtilsKt;
import net.corda.testing.node.*;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static java.util.Collections.singletonList;

public abstract class AbstractAssetJunitFlowTests {
        @NotNull
        public MockNetwork network;
        @NotNull
        public StartedMockNode lenderOfSecurity;
        @NotNull
        public StartedMockNode lenderOfCash;
        @NotNull
        public StartedMockNode globalCustodian;
        @NotNull
        public Party lenderOfSecurityParty;
        @NotNull
        public Party lenderOfCashParty;
        @NotNull
        public Party custodianParty;
        @NotNull
        private final String cusip = "CUSIP123";

        @NotNull
        public final MockNetwork getNetwork() {
            MockNetwork var10000 = this.network;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("network");
            }

            return var10000;
        }

        public final void setNetwork(@NotNull MockNetwork var1) {
            Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
            this.network = var1;
        }

        @NotNull
        public final StartedMockNode getLenderOfSecurity() {
            StartedMockNode var10000 = this.lenderOfSecurity;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfSecurity");
            }

            return var10000;
        }

        public final void setLenderOfSecurity(@NotNull StartedMockNode var1) {
            Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
            this.lenderOfSecurity = var1;
        }

        @NotNull
        public final StartedMockNode getLenderOfCash() {
            StartedMockNode var10000 = this.lenderOfCash;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfCash");
            }

            return var10000;
        }

        public final void setLenderOfCash(@NotNull StartedMockNode var1) {
            Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
            this.lenderOfCash = var1;
        }

        @NotNull
        public final StartedMockNode getGlobalCustodian() {
            StartedMockNode var10000 = this.globalCustodian;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("globalCustodian");
            }

            return var10000;
        }

        public final void setGlobalCustodian(@NotNull StartedMockNode var1) {
            Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
            this.globalCustodian = var1;
        }

        @NotNull
        public final Party getLenderOfSecurityParty() {
            Party var10000 = this.lenderOfSecurityParty;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfSecurityParty");
            }

            return var10000;
        }

        public final void setLenderOfSecurityParty(@NotNull Party var1) {
            Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
            this.lenderOfSecurityParty = var1;
        }

        @NotNull
        public final Party getLenderOfCashParty() {
            Party var10000 = this.lenderOfCashParty;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfCashParty");
            }

            return var10000;
        }

        public final void setLenderOfCashParty(@NotNull Party var1) {
            Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
            this.lenderOfCashParty = var1;
        }

        @NotNull
        public final Party getCustodianParty() {
            Party var10000 = this.custodianParty;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("custodianParty");
            }

            return var10000;
        }

        public final void setCustodianParty(@NotNull Party var1) {
            Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
            this.custodianParty = var1;
        }

        @NotNull
        protected final String getCusip() {
            return this.cusip;
        }

        @Before
        public final void setup() {
            this.network = new MockNetwork(ImmutableList.of("com.template.cordapp"), (MockNetworkParameters)null, false, true, (InMemoryMessagingNetwork.ServicePeerAllocationStrategy)null, (List)null, (NetworkParameters)null);

            MockNetwork var10001 = this.network;
            if (var10001 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("network");
            }

            this.lenderOfSecurity = network.createNode();
            var10001 = this.network;
            if (var10001 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("network");
            }

            this.lenderOfCash = network.createNode();
            var10001 = this.network;
            if (var10001 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("network");
            }

            this.globalCustodian = network.createNode();
            StartedMockNode[] var10000 = new StartedMockNode[3];
            StartedMockNode var10003 = this.lenderOfSecurity;
            if (var10003 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfSecurity");
            }

            var10000[0] = var10003;
            var10003 = this.lenderOfCash;
            if (var10003 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfCash");
            }

            var10000[1] = var10003;
            var10003 = this.globalCustodian;
            if (var10003 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("globalCustodian");
            }

            var10000[2] = var10003;
            List nodes = CollectionsKt.listOf(var10000);
            StartedMockNode var2 = this.lenderOfCash;
            if (var2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfCash");
            }

            var2.registerInitiatedFlow(CreateAssetTransferRequestResponderFlow.class);

            var2 = this.lenderOfCash;
            if (var2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfCash");
            }

            var2.registerInitiatedFlow(AssetSettlementResponderFlow.class);
            var2 = this.lenderOfSecurity;
            if (var2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfSecurity");
            }

            var2.registerInitiatedFlow(ConfirmAssetTransferRequestHandlerFlow.class);
            var2 = this.lenderOfSecurity;
            if (var2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfSecurity");
            }

            var2.registerInitiatedFlow(com.template.cordapp.seller.flows.AssetSettlementResponderFlow.class);
            var2 = this.globalCustodian;
            if (var2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("globalCustodian");
            }

            var2.registerInitiatedFlow(ConfirmAssetTransferRequestResponderFlow.class);
            StartedMockNode var3 = this.lenderOfSecurity;
            if (var3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfSecurity");
            }

            this.lenderOfSecurityParty = InternalTestUtilsKt.chooseIdentity(var3.getInfo());
            var3 = this.lenderOfCash;
            if (var3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfCash");
            }

            this.lenderOfCashParty = InternalTestUtilsKt.chooseIdentity(var3.getInfo());
            var3 = this.globalCustodian;
            if (var3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("globalCustodian");
            }

            this.custodianParty = InternalTestUtilsKt.chooseIdentity(var3.getInfo());
        }

        @After
        public final void tearDown() {
            MockNetwork var10000 = this.network;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("network");
            }

            var10000.stopNodes();
        }

        @NotNull
        protected final SignedTransaction createAsset(@NotNull StartedMockNode owner, @NotNull String cusip, @NotNull String assetName, @NotNull Amount purchaseCost) {
            Intrinsics.checkParameterIsNotNull(owner, "owner");
            Intrinsics.checkParameterIsNotNull(cusip, "cusip");
            Intrinsics.checkParameterIsNotNull(assetName, "assetName");
            Intrinsics.checkParameterIsNotNull(purchaseCost, "purchaseCost");
            CreateAssetStateFlow flow = new CreateAssetStateFlow(cusip, assetName, purchaseCost);
            Object var10000 = KotlinUtilsKt.getOrThrow(owner.startFlow((FlowLogic)flow),(Duration)null);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "owner.startFlow(flow).getOrThrow()");
            return (SignedTransaction)var10000;
        }

        @NotNull
        protected final Asset createAsset() {
            StartedMockNode var10001 = this.lenderOfSecurity;
            if (var10001 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfSecurity");
            }

            final SignedTransaction stx = this.createAsset(var10001, this.cusip, "US BOND", Currencies.DOLLARS(1000));
            MockNetwork var10000 = this.network;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("network");
            }

            var10000.waitQuiescent();
            StartedMockNode var2 = this.lenderOfSecurity;
            if (var2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lenderOfSecurity");
            }

            return (Asset)var2.transaction((Function0) new Function0() {

                @NotNull
                public final Asset invoke() {
                    ContractState var10000 = null;
                    try {
                        var10000 = AbstractAssetJunitFlowTests.this.getLenderOfSecurity().getServices().loadState(stx.getTx().outRef(0).getRef()).getData();
                    } catch (TransactionResolutionException e) {
                        e.printStackTrace();
                    }
                    if (var10000 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type com.template.cordapp.state.Asset");
                    } else {
                        Asset asset = (Asset)var10000;
                        AssertionsKt.assertEquals(asset.getCusip(), cusip, "Expected equals actual");
                        return asset;
                    }
                }
                });
        }

        @NotNull
        protected final SignedTransaction createAssetTransferRequest(@NotNull StartedMockNode lenderOfSecurity, @NotNull Party lenderOfCash, @NotNull String cusip) {
            Intrinsics.checkParameterIsNotNull(lenderOfSecurity, "lenderOfSecurity");
            Intrinsics.checkParameterIsNotNull(lenderOfCash, "lenderOfCash");
            Intrinsics.checkParameterIsNotNull(cusip, "cusip");
            CreateAssetTransferRequestInitiatorFlow flow = new CreateAssetTransferRequestInitiatorFlow(cusip, lenderOfCash);
            Object var10000 = KotlinUtilsKt.getOrThrow((Future)lenderOfSecurity.startFlow((FlowLogic)flow), (Duration)null);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "lenderOfSecurity.startFlow(flow).getOrThrow()");
            return (SignedTransaction)var10000;
        }

        @NotNull
        protected final SignedTransaction confirmAssetTransferRequest(@NotNull StartedMockNode lenderOfCash, @NotNull Party custodian, @NotNull UniqueIdentifier linearId) {
            Intrinsics.checkParameterIsNotNull(lenderOfCash, "lenderOfCash");
            Intrinsics.checkParameterIsNotNull(custodian, "custodian");
            Intrinsics.checkParameterIsNotNull(linearId, "linearId");
            ConfirmAssetTransferRequestInitiatorFlow flow = new ConfirmAssetTransferRequestInitiatorFlow(linearId, custodian);
            Object var10000 = KotlinUtilsKt.getOrThrow((Future)lenderOfCash.startFlow((FlowLogic)flow), (Duration)null);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "lenderOfCash.startFlow(flow).getOrThrow()");
            return (SignedTransaction)var10000;
        }

        @NotNull
        protected final SignedTransaction settleAssetTransferRequest(@NotNull StartedMockNode custodianNode, @NotNull UniqueIdentifier linearId) {
            Intrinsics.checkParameterIsNotNull(custodianNode, "custodianNode");
            Intrinsics.checkParameterIsNotNull(linearId, "linearId");
            AssetSettlementInitiatorFlow flow = new AssetSettlementInitiatorFlow(linearId);
            Object var10000 = KotlinUtilsKt.getOrThrow((Future)custodianNode.startFlow((FlowLogic)flow), (Duration)null);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "custodianNode.startFlow(flow).getOrThrow()");
            return (SignedTransaction)var10000;
        }

        @NotNull
        protected final SignedTransaction selfIssueCash(@NotNull StartedMockNode node, @NotNull Amount amount) throws Throwable {
            Intrinsics.checkParameterIsNotNull(node, "node");
            Intrinsics.checkParameterIsNotNull(amount, "amount");
            Party var10000 = (Party)CollectionsKt.firstOrNull(node.getServices().getNetworkMapCache().getNotaryIdentities());
            if (var10000 != null) {
                Party notary = var10000;
                OpaqueBytes issueRef = OpaqueBytes.Companion.of(new byte[]{0});
                CashIssueFlow.IssueRequest issueRequest = new CashIssueFlow.IssueRequest(amount, issueRef, notary);
                CashIssueFlow flow = new CashIssueFlow(issueRequest);
                return ((AbstractCashFlow.Result)KotlinUtilsKt.getOrThrow((Future)node.startFlow((FlowLogic)flow), (Duration)null)).getStx();
            } else {
                throw (Throwable)(new IllegalStateException("Could not find a notary."));
            }
        }

        @NotNull
        protected final Party resolveIdentity(@NotNull StartedMockNode node, @NotNull AbstractParty anonymousParty) {
            Intrinsics.checkParameterIsNotNull(node, "node");
            Intrinsics.checkParameterIsNotNull(anonymousParty, "anonymousParty");
            return node.getServices().getIdentityService().requireWellKnownPartyFromAnonymous(anonymousParty);
        }

        @NotNull
        protected final StateAndRef getStateByLinearId(@NotNull final UniqueIdentifier linearId, @NotNull final Class clazz, @NotNull final StartedMockNode mockNode) {
            Intrinsics.checkParameterIsNotNull(linearId, "linearId");
            Intrinsics.checkParameterIsNotNull(clazz, "clazz");
            Intrinsics.checkParameterIsNotNull(mockNode, "mockNode");
            final QueryCriteria.LinearStateQueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria((List)null, CollectionsKt.listOf(linearId), Vault.StateStatus.UNCONSUMED, (Set)null);
            return (StateAndRef)mockNode.transaction((Function0)(new Function0() {
                // $FF: synthetic method
                // $FF: bridge method

                @NotNull
                public final StateAndRef invoke() {
                    StateAndRef var10000 = (StateAndRef) CollectionsKt.firstOrNull(mockNode.getServices().getVaultService().queryBy(clazz, (QueryCriteria) queryCriteria).getStates());
                    if (var10000 != null) {
                        return var10000;
                    } else {
                        try {
                            throw (Throwable) (new StateNotFoundOnVaultException("State with id " + linearId + " not found."));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                    return null;
                }

            }));
        }
    }



