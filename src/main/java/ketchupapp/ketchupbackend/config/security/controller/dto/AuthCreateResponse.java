package ketchupapp.ketchupbackend.config.security.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"message", "status"})
public record AuthCreateResponse(String message, Boolean status) {
}