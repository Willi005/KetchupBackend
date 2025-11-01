package ketchupapp.ketchupbackend.dto;

import ketchupapp.ketchupbackend.model.OrderItem;
import ketchupapp.ketchupbackend.model.PaymentDetails;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        String id,
        long ticketNumber,
        String clientName,
        List<OrderItem> items,
        LocalDateTime orderTimestamp,
        String employeeName,
        PaymentDetails payment,
        double subtotal,
        double totalAmount,
        String kitchenNotes
) {}