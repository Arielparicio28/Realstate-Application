package com.round3.realestate.controller;

import com.round3.realestate.dtos.AuctionCloseResponseDto;
import com.round3.realestate.dtos.AuctionsResponseDto;
import com.round3.realestate.dtos.AuctionsRequestDto;
import com.round3.realestate.dtos.BidRequestDto;
import com.round3.realestate.entity.User;
import com.round3.realestate.services.AuctionService;
import com.round3.realestate.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auction")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;
    @Autowired
    private AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<AuctionsResponseDto> createAuction(@Valid @RequestBody AuctionsRequestDto requestDto) {
        return ResponseEntity.ok(auctionService.createAuction(requestDto));
    }

    @PostMapping("/{auctionId}/bid")
    public ResponseEntity<Void> placeBid(
            @PathVariable Long auctionId,
            @Valid @RequestBody BidRequestDto requestDto) {
        User user = authService.getAuthenticatedUser();
        auctionService.placeBid(auctionId, requestDto, user);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionsResponseDto> getAuctionDetails(@PathVariable Long auctionId) {
        return ResponseEntity.ok(auctionService.getAuctionDetails(auctionId));
    }

    @PatchMapping("/{auctionId}/close")
    public ResponseEntity<AuctionCloseResponseDto> closeAuction(@PathVariable Long auctionId) {
        return ResponseEntity.ok(auctionService.closeAuction(auctionId));
    }
}