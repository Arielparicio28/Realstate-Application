package com.round3.realestate.repository;

import com.round3.realestate.entity.Auction;
import com.round3.realestate.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid,Long> {
    /**
     * Encuentra todas las pujas para una subasta específica ordenadas por timestamp descendente (más reciente primero)
     */
    List<Bid> findByAuctionOrderByTimestampDesc(Auction auction);

    /**
     * Encuentra la puja con el monto más alto para una subasta específica
     * El metodo 'findTop' con 'OrderBy' devuelve el primer registro ordenado (en este caso, el de mayor monto)
     */
    Optional<Bid> findTopByAuctionOrderByAmountDesc(Auction auction);

    /**
     * Encuentra todas las pujas de un usuario específico
     */
    List<Bid> findByUserId(Long userId);

    /**
     * Cuenta el número de pujas para una subasta
     */
    long countByAuction(Auction auction);
}
