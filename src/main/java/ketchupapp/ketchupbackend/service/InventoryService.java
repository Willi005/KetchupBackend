package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.OrderItemRequestDto;
import ketchupapp.ketchupbackend.model.OrderItem;
import java.util.List;

public interface InventoryService {
    List<OrderItem> processOrderItems(List<OrderItemRequestDto> itemsDto);
}