package com.round3.realestate.dtos;

import lombok.Data;

@Data
public class EmploymentResponseDto {
    private EmploymentDataDto employmentData;
    private String message;
    private boolean success;
}
