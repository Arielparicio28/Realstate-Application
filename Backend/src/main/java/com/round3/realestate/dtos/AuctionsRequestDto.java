package com.round3.realestate.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class AuctionsRequestDto {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull
    @Positive
    private BigDecimal startingPrice;

    @Positive
    private BigDecimal currentHighestBid = getStartingPrice();

    @NotNull
    @Positive
    private BigDecimal minIncrement;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime startTime;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime endTime;
}