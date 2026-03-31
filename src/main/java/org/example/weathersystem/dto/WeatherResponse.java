package org.example.weathersystem.dto;

public record WeatherResponse(
        double temperature,
        String condition
) {
}