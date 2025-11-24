package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.OrderItemRequestDto;
import ketchupapp.ketchupbackend.exception.InsufficientStockException;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.model.Food;
import ketchupapp.ketchupbackend.model.OrderItem;
import ketchupapp.ketchupbackend.repo.FoodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final FoodRepository foodRepository;

    public InventoryServiceImpl(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @Override
    @Transactional
    public List<OrderItem> processOrderItems(List<OrderItemRequestDto> itemsDto) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDto itemDto : itemsDto) {
            Food food = foodRepository.findById(itemDto.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con id: " + itemDto.getFoodId()));

            if (food.getStock() < itemDto.getQuantity()) {
                throw new InsufficientStockException("Stock insuficiente para: " + food.getName());
            }

            food.setStock(food.getStock() - itemDto.getQuantity());
            foodRepository.save(food);

            orderItems.add(new OrderItem(
                    food.getId(),
                    food.getName(),
                    food.getPrice(),
                    itemDto.getQuantity()
            ));
        }
        return orderItems;
    }
}