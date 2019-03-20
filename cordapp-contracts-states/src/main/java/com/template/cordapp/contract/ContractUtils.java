package com.template.cordapp.contract;


import kotlin.collections.CollectionsKt;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;

import java.security.PublicKey;
import java.util.*;

public final class ContractUtils {

    public static final Set keysFromParticipants(ContractState state)
    {

        List stateParticipants = state.getParticipants();

        Collection participantsList = new ArrayList(stateParticipants);
        Iterator iterator = participantsList.iterator();

        while(iterator.hasNext())
        {
            Object item = iterator.next();
            AbstractParty party = (AbstractParty)item;
            PublicKey key = party.getOwningKey();
            participantsList.add(key);
        }

        return CollectionsKt.toSet(participantsList);
    }


}

