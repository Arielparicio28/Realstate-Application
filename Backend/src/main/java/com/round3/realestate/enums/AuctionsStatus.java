package com.round3.realestate.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuctionsStatus {
    OPEN("open"),
    CLOSED("closed");

    private final String value;

    AuctionsStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static AuctionsStatus fromValue(String value) {
        for (AuctionsStatus type : AuctionsStatus.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Status must be 'open' or 'closed' " + value);
    }

}
