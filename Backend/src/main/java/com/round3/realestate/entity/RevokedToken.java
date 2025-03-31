package com.round3.realestate.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "revoked_tokens")
@Data
public class RevokedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 500)  // Asegurar que el token no tenga problemas de almacenamiento
    private String token;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)  // Asegurar formato de fecha correcto
    private Date expirationDate;

    public RevokedToken() {}

    public RevokedToken(String token, String username, Date expirationDate) {
        this.token = token;
        this.username = username;
        this.expirationDate = expirationDate;
    }
}
