package com.round3.realestate.services;

import com.round3.realestate.dto.ScrapeRequestDto;
import com.round3.realestate.dto.ScrapeResponseDto;
import com.round3.realestate.dto.PropertyDataDto;
import com.round3.realestate.entity.Property;
import com.round3.realestate.repository.PropertyRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class ScrapingService {

    @Autowired
    private PropertyRepository propertyRepository;



    public ScrapeResponseDto scrapeProperty(ScrapeRequestDto requestDto) {
        try {
            Document doc = Jsoup.connect(requestDto.getUrl())
                    .userAgent("Chrome/110.0.5481.77")
                    .header("Accept-Language", "es-ES,es;q=0.9")
                    .timeout(5000)
                    .get();

            String fullTitle = doc.select("span.main-info__title-main").text();
            String type = fullTitle.isEmpty() ? "" : fullTitle.split(" ")[0];
            String location = doc.select("span.main-info__title-minor").text();

            String priceText = doc.select("span.info-data-price span.txt-bold").text()
                    .replaceAll("\\D", "");
            String formattedPrice = priceText.isEmpty() ? "0" :
                    String.format("%,d", Long.parseLong(priceText))
                            .replace(",", ".");

            double size = 0.0;
            int rooms = 0;
            Elements features = doc.select("div.info-features span");
            for (var feature : features) {
                String text = feature.text();
                if (text.contains("m²")) {
                    size = Double.parseDouble(text.replaceAll("\\D", ""));
                } else if (text.contains("hab")) {
                    rooms = Integer.parseInt(text.replaceAll("\\D+", ""));
                }
            }

            // Guardar en la base de datos si es necesario
            if (requestDto.isStore()) {
                Property property = new Property();
                property.setName(type);
                property.setFullTitle(fullTitle);
                property.setLocation(location);
                property.setPrice(new BigDecimal(priceText));
                property.setSize(size);
                property.setRooms(rooms);
                propertyRepository.save(property);
            }

            // Crear el PropertyDataDto con los datos formateados
            PropertyDataDto propertyData = new PropertyDataDto(
                    type,
                    fullTitle,
                    location,
                    formattedPrice,
                    String.format("%.0f m²", size),
                    rooms + " hab.",
                    requestDto.getUrl()
            );

            return new ScrapeResponseDto(propertyData, requestDto.isStore());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}