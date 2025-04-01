package com.round3.realestate.dtos;


import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ScrapeResponseDto {

    private PropertyDataDto data;
    private boolean saved;


}

