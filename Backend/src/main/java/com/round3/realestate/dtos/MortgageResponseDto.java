package com.round3.realestate.dtos;

import lombok.Data;

@Data
public class MortgageResponseDto {
    private boolean approved;
    private Long mortgageId;
    private double netMonthly;
    private double monthlyPayment;
    private String allowedPercentage;
    private String message;
    private Integer numberOfMonths;
}
