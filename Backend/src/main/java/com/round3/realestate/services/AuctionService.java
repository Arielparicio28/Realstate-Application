package com.round3.realestate.services;

import com.round3.realestate.dtos.AuctionCloseResponseDto;
import com.round3.realestate.dtos.AuctionsResponseDto;
import com.round3.realestate.dtos.AuctionsRequestDto;
import com.round3.realestate.dtos.BidDto;
import com.round3.realestate.dtos.BidRequestDto;
import com.round3.realestate.entity.Auction;
import com.round3.realestate.entity.Bid;
import com.round3.realestate.entity.Property;
import com.round3.realestate.entity.User;
import com.round3.realestate.enums.AuctionsStatus;
import com.round3.realestate.messaging.BidMessage;
import com.round3.realestate.repository.AuctionsRepository;
import com.round3.realestate.repository.BidRepository;
import com.round3.realestate.repository.PropertyRepository;
import com.round3.realestate.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuctionService {
    private final AuctionsRepository auctionRepository;
    private final PropertyRepository propertyRepository;
    private final BidRepository bidRepository;
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingkey;

    public AuctionService(
            AuctionsRepository auctionRepository,
            PropertyRepository propertyRepository,
            BidRepository bidRepository,
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange}") String exchange,
            @Value("${rabbitmq.routingkey}") String routingkey) {
        this.auctionRepository = auctionRepository;
        this.propertyRepository = propertyRepository;
        this.bidRepository = bidRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingkey = routingkey;
    }

    public AuctionsResponseDto createAuction(AuctionsRequestDto requestDto) {
        Property property = propertyRepository.findById(requestDto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Verificar si ya existe una subasta para esta propiedad
        if (auctionRepository.existsByProperty(property)) {
            throw new IllegalStateException("An auction already exists for this property");
        }


        BigDecimal propertyPrice;
        try {
            String priceStr = String.valueOf(property.getPrice());
            if (priceStr == null || priceStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Property price is missing");
            }

            // Intentar parsear directamente primero (si ya es un formato numérico válido)
            try {
                propertyPrice = new BigDecimal(priceStr);
            } catch (NumberFormatException e) {
                // Si falla, intentar convertir desde formato español "123.456,78" -> "123456.78"
                priceStr = priceStr.replace(".", "").replace(",", ".");
                propertyPrice = new BigDecimal(priceStr);
            }
        } catch (Exception e) {
            log.error("Error parsing property price: {}", property.getPrice(), e);
            // Valor predeterminado o lanzar excepción según lo que prefieras
            throw new IllegalArgumentException("Invalid property price format: " + property.getPrice());
        }

        // Calcula el precio inicial como el 70% del valor de la propiedad
        BigDecimal startingPrice = propertyPrice.multiply(new BigDecimal("0.7"))
                .setScale(2, RoundingMode.HALF_UP);

        // Calcular el incremento mínimo como el 5% del precio inicial
        BigDecimal minimumBidIncrement = startingPrice.multiply(new BigDecimal("0.05"))
                .setScale(2, RoundingMode.HALF_UP);

        Auction auction = new Auction();
        auction.setProperty(property);
        auction.setStartTime(requestDto.getStartTime());
        auction.setEndTime(requestDto.getEndTime());
        auction.setStartingPrice(startingPrice);
        auction.setMinimumBidIncrement(minimumBidIncrement);
        auction.setCurrentHighestBid(startingPrice); // La puja más alta inicial es el precio inicial
        auction.setStatus(AuctionsStatus.OPEN);

        auction = auctionRepository.save(auction);
        return mapToAuctionResponseDto(auction);
    }

    public void placeBid(Long auctionId, BidRequestDto requestDto, User user) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        if (auction.getStatus() != AuctionsStatus.OPEN) {
            throw new IllegalStateException("Auction is not open");
        }

        // Verificar si la subasta está dentro del período válido
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
            throw new IllegalStateException("Auction is not active at this time");
        }

        // Verificar que la puja sea mayor que la actual más el incremento mínimo
        if (auction.getCurrentHighestBid().add(auction.getMinimumBidIncrement()).compareTo(requestDto.getAmount()) > 0) {
            throw new IllegalArgumentException("Bid amount must be at least " +
                    auction.getCurrentHighestBid().add(auction.getMinimumBidIncrement()) +
                    " (current highest bid + minimum increment)");
        }

        // Crear mensaje de puja para RabbitMQ
        BidMessage bidMessage = new BidMessage(
                auctionId,
                user.getId(),
                requestDto.getAmount(),
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        // Enviar mensaje a RabbitMQ
        log.info("Sending bid message to RabbitMQ: {}", bidMessage);
        rabbitTemplate.convertAndSend(exchange, routingkey, bidMessage);
        log.info("Bid message sent successfully");
    }

    public AuctionsResponseDto getAuctionDetails(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        return mapToAuctionResponseDto(auction);
    }

    public AuctionCloseResponseDto closeAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        if (auction.getStatus() != AuctionsStatus.OPEN) {
            throw new IllegalStateException("Auction is already closed");
        }

        // Cambiar estado de la subasta a cerrado
        auction.setStatus(AuctionsStatus.CLOSED);

        // Actualizar disponibilidad de la propiedad
        Property property = auction.getProperty();
        property.setAvailability("Unavailable");
        propertyRepository.save(property);

        // Buscar la puja ganadora (la más alta)
        Bid winningBid = bidRepository.findTopByAuctionOrderByAmountDesc(auction)
                .orElse(null);

        // Si hay una puja ganadora, actualizar el usuario ganador
        if (winningBid != null) {
            auction.setWinningUser(winningBid.getUser());
            auction.setCurrentHighestBid(winningBid.getAmount());
        }

        auction = auctionRepository.save(auction);

        // Crear y devolver respuesta
        AuctionCloseResponseDto responseDto = new AuctionCloseResponseDto();
        responseDto.setWinningBidAmount(auction.getCurrentHighestBid());
        responseDto.setWinningUserId(auction.getWinningUser() != null ? auction.getWinningUser().getId() : null);

        return responseDto;
    }

    // Metodo auxiliar para mapear de Auction a AuctionResponseDto
    private AuctionsResponseDto mapToAuctionResponseDto(Auction auction) {
        AuctionsResponseDto responseDto = new AuctionsResponseDto();
        responseDto.setId(auction.getId());
        responseDto.setPropertyId(auction.getProperty().getId());
        responseDto.setStartTime(auction.getStartTime());
        responseDto.setEndTime(auction.getEndTime());
        responseDto.setStartingPrice(auction.getStartingPrice());
        responseDto.setMinimumBidIncrement(auction.getMinimumBidIncrement());
        responseDto.setCurrentHighestBid(auction.getCurrentHighestBid());
        responseDto.setStatus(auction.getStatus().toString());

        // Obtener todas las pujas para esta subasta
        List<Bid> bids = bidRepository.findByAuctionOrderByTimestampDesc(auction);
        List<BidDto> bidDtos = bids.stream().map(bid -> {
            BidDto bidDto = new BidDto();
            bidDto.setId(bid.getId());
            bidDto.setUserId(bid.getUser().getId());
            bidDto.setAmount(bid.getAmount());
            bidDto.setTimestamp(bid.getTimestamp());
            return bidDto;
        }).collect(Collectors.toList());

        responseDto.setBids(bidDtos);

        return responseDto;
    }
}