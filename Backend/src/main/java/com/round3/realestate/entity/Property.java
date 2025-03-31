package com.round3.realestate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "properties")
@Data
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String fullTitle;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private BigDecimal price;

    private Double size;

    private Integer rooms;

    // Campo adicional para la disponibilidad
    @Column(nullable = false)
    private String availability = "Available";
}
