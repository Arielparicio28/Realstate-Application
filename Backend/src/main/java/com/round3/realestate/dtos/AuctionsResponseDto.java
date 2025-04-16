package com.round3.realestate.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class AuctionsResponseDto {

    private Long id;
    private Long propertyId;
    private String status;
    private BigDecimal currentHighestBid;
    private BigDecimal minIncrement;
    private BigDecimal startingPrice;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;


}
