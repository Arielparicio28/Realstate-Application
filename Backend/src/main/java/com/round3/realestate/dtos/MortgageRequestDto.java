package com.round3.realestate.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MortgageRequestDto {
    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Term in years is required")
    @Min(value = 15, message = "Term must be at least 15 years")
    private Integer termYears;
}
