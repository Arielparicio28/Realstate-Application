package com.round3.realestate.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BidRequestDto {
    @NotNull(message = "Bid amount is required")
    @Positive(message = "Bid amount must be positive")
    private BigDecimal amount;
}