package com.template.cordapp.flows;

import com.template.cordapp.flows.FlowLogicCommonMethods.DefaultImpls;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;
import org.jetbrains.annotations.NotNull;

@InitiatingFlow
@Metadata(
        mv = {1, 1, 8},
        bv = {1, 0, 2},
        k = 1,
        d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b'\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\u00020\u0003B\u0005¢\u0006\u0002\u0010\u0004¨\u0006\u0005"},
        d2 = {"Lcom/synechron/cordapp/flows/AbstractAssetSettlementFlow;", "T", "Lnet/corda/core/flows/FlowLogic;", "Lcom/synechron/cordapp/flows/FlowLogicCommonMethods;", "()V", "cordapp-contracts-states"}
)
public abstract class AbstractConfirmAssetTransferRequestFlow extends FlowLogic implements FlowLogicCommonMethods {
    @NotNull
    public Party firstNotary(@NotNull ServiceHub $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return DefaultImpls.firstNotary(this, $receiver);
    }

    @NotNull
    public StateAndRef loadState(@NotNull ServiceHub $receiver, @NotNull UniqueIdentifier linearId, @NotNull Class clazz) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(linearId, "linearId");
        Intrinsics.checkParameterIsNotNull(clazz, "clazz");
        return DefaultImpls.loadState(this, $receiver, linearId, clazz);
    }

    @NotNull
    public Party resolveIdentity(@NotNull ServiceHub $receiver, @NotNull AbstractParty abstractParty) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(abstractParty, "abstractParty");
        return DefaultImpls.resolveIdentity(this, $receiver, abstractParty);
    }
}


