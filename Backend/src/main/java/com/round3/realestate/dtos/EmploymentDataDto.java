package com.round3.realestate.dto;

import com.round3.realestate.enums.EmploymentStatus;
import lombok.Data;

@Data
public class EmploymentDataDto {
    private Long id;
    private String contract;
    private double salary;
    private double netMonthly;
    private EmploymentStatus employmentStatus;
}
