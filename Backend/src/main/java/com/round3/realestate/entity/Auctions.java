package com.round3.realestate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "auctions")
@Data
public class Auctions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "status")
    private String status;

    @Column(name = "current_highest_bid", precision = 10, scale = 2)
    private BigDecimal currentHighestBid;

    @Column(name = "min_increment",precision = 10, scale = 2)
    private BigDecimal minIncrement;

    @Column(name = "starting_price", precision = 10, scale = 2)
    private BigDecimal startingPrice;

    @Column(name = "start_time")
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;


}
