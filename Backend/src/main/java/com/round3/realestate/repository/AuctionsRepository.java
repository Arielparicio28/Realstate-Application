package com.round3.realestate.repository;

import com.round3.realestate.entity.Auctions;
import com.round3.realestate.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionsRepository extends JpaRepository <Auctions,Long> {
    List<Auctions> findByProperty(Property property);
}
