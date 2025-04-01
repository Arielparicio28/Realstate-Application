package com.round3.realestate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
/*
nullable = false indica que una variable o columna no puede tener un valor nulo.
 */
@Entity
@Table(name = "properties")
@Data
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column( name = "name")
    private String name;

    @Column( name = "full_title")
    private String fullTitle;

    @Column( name = "location")
    private String location;

    @Column( name = "price")
    private BigDecimal price;

    @Column(name = "size")
    private Double size;

    @Column(name = "rooms")
    private Integer rooms;

    // Campo adicional para la disponibilidad
    @Column(name = "availability")
    private String availability = "Available";
}
