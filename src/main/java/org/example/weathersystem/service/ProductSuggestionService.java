package org.example.weathersystem.service;

import org.example.weathersystem.dto.WeatherResponse;
import org.springframework.stereotype.Service;

@Service
public class ProductSuggestionService {

    public String suggestProduct(WeatherResponse weather) {
        return switch (weather.condition().toLowerCase()) {
            case "rain" -> "umbrella";
            case "sunny" -> "sunglasses";
            case "snow" -> "jacket";
            default -> throw new IllegalArgumentException("unsupported weather condition: " + weather.condition());
        };
    }
}