package com.round3.realestate.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MortgageDto {
    private Long id;
    private Long propertyId;
    private String propertyName;
    private String propertyLocation;
    private BigDecimal totalCost;
    private BigDecimal monthlyPayment;
    private int numberOfMonths;
    private double interestRate;
}

