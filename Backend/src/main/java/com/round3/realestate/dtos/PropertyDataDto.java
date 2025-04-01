package com.round3.realestate.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PropertyDataDto {

    private String type;
    private String fullTitle;
    private String location;
    private String price;
    private String size;
    private String rooms;
    private String url;
}