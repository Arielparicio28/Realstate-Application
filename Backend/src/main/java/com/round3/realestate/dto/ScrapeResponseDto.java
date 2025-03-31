package com.round3.realestate.dto;


import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ScrapeResponseDto {

    private PropertyDataDto data;
    private boolean saved;


}

