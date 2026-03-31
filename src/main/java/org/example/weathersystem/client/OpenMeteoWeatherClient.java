package org.example.weathersystem.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.weathersystem.dto.WeatherResponse;
import org.example.weathersystem.exception.WeatherApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class OpenMeteoWeatherClient implements WeatherClient {

    private final RestClient geocodingClient;
    private final RestClient forecastClient;

    public OpenMeteoWeatherClient() {
        this.geocodingClient = RestClient.builder()
                .baseUrl("https://geocoding-api.open-meteo.com")
                .build();

        this.forecastClient = RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
    }

    @Override
    public WeatherResponse getWeather(String city) {
        try {
            GeocodingResponse geocodingResponse = geocodingClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/search")
                            .queryParam("name", city)
                            .queryParam("count", 1)
                            .build())
                    .retrieve()
                    .body(GeocodingResponse.class);

            if (geocodingResponse == null || geocodingResponse.results() == null || geocodingResponse.results().isEmpty()) {
                throw new WeatherApiException("City not found: " + city);
            }

            LocationResult location = geocodingResponse.results().get(0);

            ForecastResponse forecastResponse = forecastClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", location.latitude())
                            .queryParam("longitude", location.longitude())
                            .queryParam("current", "temperature_2m,weather_code")
                            .build())
                    .retrieve()
                    .body(ForecastResponse.class);

            if (forecastResponse == null || forecastResponse.current() == null) {
                throw new WeatherApiException("Weather data is missing for city: " + city);
            }

            String condition = mapWeatherCodeToCondition(forecastResponse.current().weatherCode());

            return new WeatherResponse(
                    forecastResponse.current().temperature2m(),
                    condition
            );
        } catch (RestClientException e) {
            throw new WeatherApiException("Failed to fetch weather for city: " + city, e);
        }
    }

    private String mapWeatherCodeToCondition(int weatherCode) {
        if (isSnow(weatherCode)) {
            return "snow";
        }
        if (isRain(weatherCode)) {
            return "rain";
        }
        return "sunny";
    }

    private boolean isRain(int code) {
        return code == 51 || code == 53 || code == 55
                || code == 56 || code == 57
                || code == 61 || code == 63 || code == 65
                || code == 66 || code == 67
                || code == 80 || code == 81 || code == 82
                || code == 95 || code == 96 || code == 99;
    }

    private boolean isSnow(int code) {
        return code == 71 || code == 73 || code == 75
                || code == 77
                || code == 85 || code == 86;
    }

    private record GeocodingResponse(List<LocationResult> results) {
    }

    private record LocationResult(
            String name,
            double latitude,
            double longitude
    ) {
    }

    private record ForecastResponse(CurrentWeather current) {
    }

    private record CurrentWeather(
            @JsonProperty("temperature_2m")
            double temperature2m,

            @JsonProperty("weather_code")
            int weatherCode
    ) {
    }
}