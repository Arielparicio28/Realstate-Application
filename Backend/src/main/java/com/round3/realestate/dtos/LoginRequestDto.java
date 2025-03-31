package com.round3.realestate.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "Email or username is required")
    private String emailOrUsername;

    @NotBlank(message = "Password is required")
    private String password;
}
