package com.round3.realestate.controller;

import com.round3.realestate.dtos.MortgageRequestDto;
import com.round3.realestate.dtos.MortgageResponseDto;
import com.round3.realestate.services.MortgageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MortgageController {

    @Autowired
    private MortgageService mortgageService;

    @PostMapping("/mortgage")
    public MortgageResponseDto processMortgageRequest(@Valid @RequestBody MortgageRequestDto requestDto) {
        return mortgageService.processMortgageRequest(requestDto);
    }
}