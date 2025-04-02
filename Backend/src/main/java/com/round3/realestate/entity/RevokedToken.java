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
    @Column(nullable = false)
    private Long id;

    @Column(unique = true, nullable = false, length = 500, name = "token")  // Asegurar que el token no tenga problemas de almacenamiento
    private String token;

    @Column(nullable = false, name = "username")
    private String username;

    @Column(nullable = false, name = "expiration_date")
    @Temporal(TemporalType.TIMESTAMP)  // Asegurar formato de fecha correcto
    private Date expirationDate;

    public RevokedToken() {}

    public RevokedToken(String token, String username, Date expirationDate) {
        this.token = token;
        this.username = username;
        this.expirationDate = expirationDate;
    }
}
