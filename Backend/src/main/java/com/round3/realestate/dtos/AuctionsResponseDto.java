package com.round3.realestate.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class AuctionsResponseDto {

    private Long id;
    private Long propertyId;
    private String status;
    private BigDecimal currentHighestBid;
    private BigDecimal minimumBidIncrement;
    private BigDecimal startingPrice;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private List<BidDto> bids;

}
