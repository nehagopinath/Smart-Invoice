package com.template.cordapp.flows;

import com.template.cordapp.flows.FlowLogicCommonMethods.DefaultImpls;
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
public abstract class AbstractConfirmAssetTransferRequestFlow<SignedTransaction> extends FlowLogic<SignedTransaction> implements FlowLogicCommonMethods {
    @NotNull
    public Party firstNotary(@NotNull ServiceHub $receiver) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return DefaultImpls.firstNotary( $receiver);
    }

    @NotNull
    public StateAndRef loadState(@NotNull ServiceHub $receiver, @NotNull UniqueIdentifier linearId, @NotNull Class clazz) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(linearId, "linearId");
        Intrinsics.checkParameterIsNotNull(clazz, "clazz");
        return DefaultImpls.loadState( $receiver, linearId, clazz);
    }

    @NotNull
    public Party resolveIdentity(@NotNull ServiceHub $receiver, @NotNull AbstractParty abstractParty) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        Intrinsics.checkParameterIsNotNull(abstractParty, "abstractParty");
        return DefaultImpls.resolveIdentity($receiver, abstractParty);
    }
}


