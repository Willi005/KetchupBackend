package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.OrderItemRequestDto;
import ketchupapp.ketchupbackend.exception.InsufficientStockException;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.model.Food;
import ketchupapp.ketchupbackend.model.OrderItem;
import ketchupapp.ketchupbackend.repo.FoodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final FoodRepository foodRepository;

    public InventoryServiceImpl(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @Override
    @Transactional
    public List<OrderItem> processOrderItems(List<OrderItemRequestDto> itemsDto) {
        // Log de diagnóstico
        log.debug("INVENTARIO: Verificando stock para {} items...", itemsDto.size());

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDto itemDto : itemsDto) {
            Food food = foodRepository.findById(itemDto.getFoodId())
                    .orElseThrow(() -> {
                        log.error("INVENTARIO: Comida ID {} no existe en BD", itemDto.getFoodId());
                        return new ResourceNotFoundException("Comida no encontrada con id: " + itemDto.getFoodId());
                    });

            // Log de detalle por producto
            log.debug("INVENTARIO: Producto '{}' | Stock actual: {} | Solicitado: {}",
                    food.getName(), food.getStock(), itemDto.getQuantity());

            if (food.getStock() < itemDto.getQuantity()) {
                log.warn("INVENTARIO: Stock insuficiente para '{}'. Faltan: {}",
                        food.getName(), itemDto.getQuantity() - food.getStock());
                throw new InsufficientStockException("Stock insuficiente para: " + food.getName());
            }

            food.setStock(food.getStock() - itemDto.getQuantity());
            foodRepository.save(food);

            log.debug("INVENTARIO: Stock descontado. Nuevo stock de '{}': {}",
                    food.getName(), food.getStock());

            orderItems.add(new OrderItem(
                    food.getId(),
                    food.getName(),
                    food.getPrice(),
                    itemDto.getQuantity()
            ));
        }

        log.info("INVENTARIO: Verificación y descuento completados exitosamente.");
        return orderItems;
    }
}