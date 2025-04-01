package com.round3.realestate.dtos;

// Dto de entrada para hacer web scrapping

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapeRequestDto {
    private String url;
    private boolean store;
}
