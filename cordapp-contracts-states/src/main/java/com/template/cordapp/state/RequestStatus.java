package com.template.cordapp.state;

import com.fasterxml.jackson.annotation.JsonValue;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;


@CordaSerializable
public enum RequestStatus {
    PENDING_CONFIRMATION("Pending Confirmation"), //Initial status
    PENDING("Pending"), // updated by buyer
    TRANSFERRED("Transferred"), // on valid asset data clearing house update this status
    REJECTED("Rejected"), // on invalid asset data clearing house reject transaction with this status.
    FAILED("Failed"); // on fail of settlement e.g. with insufficient cash from Buyer party.

    @JsonValue
    @NotNull
    private String value;

    // getter method
    @NotNull
    public String getValue()
    {
        return this.value;
    }

    RequestStatus(String value) {
        this.value = value;
    }
}
