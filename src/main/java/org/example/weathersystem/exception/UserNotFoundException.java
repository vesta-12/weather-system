package org.example.weathersystem.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("user with id " + userId + " was not found");
    }
}