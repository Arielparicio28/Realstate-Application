package com.round3.realestate.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EmploymentStatus {
    UNEMPLOYED("unemployed"),
    EMPLOYED("employed");

    private final String value;

   EmploymentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static EmploymentStatus fromValue(String value) {
        for (EmploymentStatus type : EmploymentStatus.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Contract must be 'indefinite' or 'temporary' " + value);
    }

}
