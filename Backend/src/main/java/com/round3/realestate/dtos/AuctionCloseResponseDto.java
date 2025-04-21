package com.round3.realestate.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionCloseResponseDto {
    private BigDecimal winningBidAmount;
    private Long winningUserId;
}