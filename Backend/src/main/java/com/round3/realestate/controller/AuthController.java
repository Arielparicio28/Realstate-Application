package com.round3.realestate.controller;


import com.round3.realestate.dto.LoginRequestDto;
import com.round3.realestate.dto.RegisterRequestDto;
import com.round3.realestate.response.LoginResponse;
import com.round3.realestate.response.RegisterResponse;
import com.round3.realestate.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        authService.registerUser(registerRequestDto);
        return ResponseEntity.ok(new RegisterResponse(true,"User successfully registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequestDto requestDto) {
        String token = authService.login(requestDto.getEmailOrUsername(), requestDto.getPassword());
        return ResponseEntity.ok(new LoginResponse(token,"Bearer"));
    }

}
