package com.round3.realestate.entity;

import com.round3.realestate.enums.ContractType;
import com.round3.realestate.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "employment")
@Data
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Column(name = "contract_type")
    @Enumerated(EnumType.STRING)
    private ContractType contract; // "indefinite", "temporary" o null

    @Column(name = "salary", nullable = false)
    private double salary;

    @Column(name = "net_monthly",nullable = false)
    private double netMonthly;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status",nullable = false)
    private EmploymentStatus employmentStatus;

}
