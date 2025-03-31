package com.round3.realestate.dto;


import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class EmploymentRequestDto {

    private String contract;

    @Min(value = 0, message = "Salary must be a positive number")
    private double salary;
}
