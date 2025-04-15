package com.round3.realestate.entity;

import com.round3.realestate.enums.ContractType;
import com.round3.realestate.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employment")
@Data
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "contract")
    @Enumerated(EnumType.STRING)
    private ContractType contract; // "indefinite", "temporary" o null

    @Column(name = "salary")
    private double salary;

    @Column(name = "net_monthly")
    private double netMonthly;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

}
