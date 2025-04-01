package com.round3.realestate.controller;

import com.round3.realestate.dtos.EmploymentRequestDto;
import com.round3.realestate.dtos.EmploymentResponseDto;
import com.round3.realestate.services.EmploymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmploymentController {

    @Autowired
    private EmploymentService employmentService;

    @PostMapping("/employment")
    public ResponseEntity<EmploymentResponseDto> saveEmploymentData(@Valid @RequestBody EmploymentRequestDto requestDto) {
        EmploymentResponseDto response = employmentService.createEmployment(requestDto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/employment")
    public EmploymentResponseDto updateEmploymentData(@Valid @RequestBody EmploymentRequestDto requestDto) {
        return employmentService.updateEmployment(requestDto);
    }
}