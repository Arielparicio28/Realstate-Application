package com.round3.realestate.repository;

import com.round3.realestate.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    Optional<RevokedToken> findByToken(String token);
}
