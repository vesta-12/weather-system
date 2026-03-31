package org.example.weathersystem.client;

import org.example.weathersystem.dto.WeatherResponse;

public interface WeatherClient {
    WeatherResponse getWeather(String city);
}