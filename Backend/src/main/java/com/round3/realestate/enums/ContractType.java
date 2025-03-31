package com.round3.realestate.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContractType {
    INDEFINITE("indefinite"),
    TEMPORARY("temporary");

    private final String value;

    ContractType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static ContractType fromValue(String value) {
        for (ContractType type : ContractType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Contract must be 'indefinite' or 'temporary' " + value);
    }

}