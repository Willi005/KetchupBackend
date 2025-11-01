package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.OrderRequestDto;
import ketchupapp.ketchupbackend.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    OrderResponseDto getOrderById(String id);

    List<OrderResponseDto> getAllOrders();
}