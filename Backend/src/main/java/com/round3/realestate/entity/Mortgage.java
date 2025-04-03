package com.round3.realestate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mortgages")
@Data
public class Mortgage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "property_id",nullable = false)
    private Property property;

    @Column(nullable = false,precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "monthly_payment",precision = 10, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(name = "number_of_months" )
    private int numberOfMonths;

    @Column(name = "interest_rate")
    private double interestRate = 0.02; // 2% anual

    @Column(name = "allowed_percentage")
    private double allowedPercentage;

    @Column(name = "date_of_approved")
    private LocalDateTime approvalDate = LocalDateTime.now();
}