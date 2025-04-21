package com.round3.realestate.repository;

import com.round3.realestate.entity.Auction;
import com.round3.realestate.entity.Property;
import com.round3.realestate.enums.AuctionsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AuctionsRepository extends JpaRepository <Auction,Long> {

    //Encuentra todas las subastas activas (estado OPEN)
    List<Auction> findByStatus(AuctionsStatus status);

    //Encuentra subastas por propiedad
    List<Auction> findByProperty(Property property);

    //Encuentra subastas activas que terminan antes de una fecha determinada
    List<Auction> findByStatusAndEndTimeBefore(AuctionsStatus status, OffsetDateTime endTime);

    //para no hacer subastas duplicadas a una propiedad.
    boolean existsByProperty(Property property);
}
