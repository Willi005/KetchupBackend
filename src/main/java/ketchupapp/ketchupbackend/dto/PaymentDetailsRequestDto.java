package ketchupapp.ketchupbackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ketchupapp.ketchupbackend.model.PaymentType;
import lombok.Data;

@Data
public class PaymentDetailsRequestDto {
    @NotNull
    private PaymentType type;

    @PositiveOrZero
    private double amountPaid;
}