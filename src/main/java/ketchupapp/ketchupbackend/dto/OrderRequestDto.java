package ketchupapp.ketchupbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    // SUJETO A CAMBIOS POR FALTA DE SPRING SECURITY
    @NotBlank
    private String employeeId;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String clientName;

    @NotEmpty
    @Valid
    private List<OrderItemRequestDto> items;

    @NotNull
    @Valid
    private PaymentDetailsRequestDto payment;
    private String kitchenNotes;
}