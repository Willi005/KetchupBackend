package ketchupapp.ketchupbackend.config.security.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"message", "jwt"})
public record AuthResponse(String message, String jwt) {
}