package com.round3.realestate.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidMessage implements Serializable {
    private Long auctionId;
    private Long userId;
    private BigDecimal amount;
    private OffsetDateTime timestamp;
}