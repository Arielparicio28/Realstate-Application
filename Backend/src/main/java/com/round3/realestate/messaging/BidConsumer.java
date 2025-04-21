package com.round3.realestate.messaging;

import com.round3.realestate.entity.Auction;
import com.round3.realestate.entity.Bid;
import com.round3.realestate.entity.User;
import com.round3.realestate.exceptions.ResourceNotFoundException;
import com.round3.realestate.repository.AuctionsRepository;
import com.round3.realestate.repository.BidRepository;
import com.round3.realestate.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BidConsumer {
    private final AuctionsRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;

    public BidConsumer(
            AuctionsRepository auctionRepository,
            BidRepository bidRepository,
            UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void consumeBidMessage(BidMessage message) throws InterruptedException {
        log.info("Received bid message: {}", message);

        // Simulación de delay para testing
        Thread.sleep(1000);

        try {
            Auction auction = auctionRepository.findById(message.getAuctionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

            User user = userRepository.findById(message.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Verificar si la puja es mayor que la actual
            if (message.getAmount().compareTo(auction.getCurrentHighestBid()) > 0) {
                log.info("Updating highest bid for auction {}: {}", auction.getId(), message.getAmount());

                // Actualizar la puja más alta en la subasta
                auction.setCurrentHighestBid(message.getAmount());
                auctionRepository.save(auction);

                // Guardar la puja
                Bid bid = new Bid();
                bid.setAuction(auction);
                bid.setUser(user);
                bid.setAmount(message.getAmount());
                bid.setTimestamp(message.getTimestamp());
                bidRepository.save(bid);

                log.info("Bid processed successfully");
            } else {
                log.info("Bid amount {} is not higher than current highest bid {}",
                        message.getAmount(), auction.getCurrentHighestBid());
            }
        } catch (Exception e) {
            log.error("Error processing bid message", e);
            // En un entorno de producción, considerar reenviar el mensaje a una cola de errores
        }
    }
}