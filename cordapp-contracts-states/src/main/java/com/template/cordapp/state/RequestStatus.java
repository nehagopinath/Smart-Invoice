package com.template.cordapp.state;

import net.corda.core.serialization.CordaSerializable;


@CordaSerializable
public enum RequestStatus {
    PENDING_CONFIRMATION("Pending Confirmation"), //Initial status
    PENDING("Pending"), // updated by buyer
    TRANSFERRED("Transferred"), // on valid asset data clearing house update this status
    REJECTED("Rejected"), // on invalid asset data clearing house reject transaction with this status.
    FAILED("Failed"); // on fail of settlement e.g. with insufficient cash from Buyer party.

    private String value;

    // getter method
    public String getValue()
    {
        return this.value;
    }

    private RequestStatus(String value) {
        this.value = value;
    }
}