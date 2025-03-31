package com.round3.realestate.controller;

import com.round3.realestate.dto.ScrapeRequestDto;
import com.round3.realestate.dto.ScrapeResponseDto;
import com.round3.realestate.services.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ScrapingController {

    @Autowired
    private ScrapingService scrapingService;

    @PostMapping("/scrape")
    public ResponseEntity<ScrapeResponseDto> scrapeProperty(@RequestBody ScrapeRequestDto requestDto) {
        ScrapeResponseDto response = scrapingService.scrapeProperty(requestDto);
        return ResponseEntity.ok(response);
    }
}
