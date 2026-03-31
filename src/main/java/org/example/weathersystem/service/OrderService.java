package org.example.weathersystem.service;

import lombok.RequiredArgsConstructor;
import org.example.weathersystem.client.WeatherClient;
import org.example.weathersystem.dto.WeatherResponse;
import org.example.weathersystem.entity.AppUser;
import org.example.weathersystem.entity.OrderEntity;
import org.example.weathersystem.exception.UserNotFoundException;
import org.example.weathersystem.repository.AppUserRepository;
import org.example.weathersystem.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final AppUserRepository appUserRepository;
    private final OrderRepository orderRepository;
    private final WeatherClient weatherClient;
    private final ProductSuggestionService productSuggestionService;

    @Transactional
    public OrderEntity createOrder(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        WeatherResponse weather = weatherClient.getWeather(user.getCity());
        String product = productSuggestionService.suggestProduct(weather);

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        return orderRepository.save(order);
    }
}