package com.round3.realestate.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResponseDto {
    private EmploymentDataDto employmentData;
    private List<MortgageDto> mortgages;
}

