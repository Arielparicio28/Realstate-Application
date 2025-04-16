package com.round3.realestate.repository;

import com.round3.realestate.entity.Auctions;
import com.round3.realestate.entity.Bid;
import com.round3.realestate.entity.Property;
import com.round3.realestate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid,Long> {
    List<Auctions> findByAuctions(Auctions auctions);
    List<User> findByUser(User user);
}
