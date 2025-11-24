package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.OrderItemRequestDto;
import ketchupapp.ketchupbackend.dto.OrderRequestDto;
import ketchupapp.ketchupbackend.dto.OrderResponseDto;
import ketchupapp.ketchupbackend.exception.InsufficientPaymentException;
import ketchupapp.ketchupbackend.exception.InsufficientStockException;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.model.*;
import ketchupapp.ketchupbackend.repo.FoodRepository;
import ketchupapp.ketchupbackend.repo.OrderRepository;
import ketchupapp.ketchupbackend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, FoodRepository foodRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.foodRepository = foodRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional  // Esta etiqueta sirve para revertir tod/o en caso de que algo falle.
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        // 1. Obtención de empleado
        User employee = userRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + dto.getEmployeeId()));

        // 2. Procesar Items (Inventario) - Refactorizado
        List<OrderItem> orderItems = processOrderItems(dto.getItems());

        // 3. Calcular Totales - Refactorizado
        double subtotal = calculateSubtotal(orderItems);
        double totalAmount = subtotal; // Aquí podrías sumar impuestos si fuera necesario

        // 4. Procesar Pago - Refactorizado
        PaymentDetails paymentDetails = processPayment(dto.getPayment(), totalAmount);

        // 5. Generación de ticket y guardado
        long ticketNumber = orderRepository.count() + 1;

        Order order = new Order();
        order.setTicketNumber(ticketNumber);
        order.setClientName(dto.getClientName());
        order.setEmployeeId(employee.getId());
        order.setEmployeeName(employee.getName());
        order.setItems(orderItems);
        order.setPayment(paymentDetails);
        order.setSubtotal(subtotal);
        order.setTotalAmount(totalAmount);
        order.setOrderTimestamp(LocalDateTime.now());
        order.setKitchenNotes(dto.getKitchenNotes());

        Order savedOrder = orderRepository.save(order);

        return mapToResponseDto(savedOrder);
    }

    @Override
    public OrderResponseDto getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con id: " + id));
        return mapToResponseDto(order);
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    // MAPPER
    private OrderResponseDto mapToResponseDto(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getTicketNumber(),
                order.getClientName(),
                order.getItems(),
                order.getOrderTimestamp(),
                order.getEmployeeName(),
                order.getPayment(),
                order.getSubtotal(),
                order.getTotalAmount(),
                order.getKitchenNotes()
        );
    }
    // TÉCNICA DE REFACTORING: EXTRACT METHOD
    // Propósito: Aislar la lógica de validación y actualización de stock.
    private List<OrderItem> processOrderItems(List<OrderItemRequestDto> itemsDto) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDto itemDto : itemsDto) {
            Food food = foodRepository.findById(itemDto.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con id: " + itemDto.getFoodId()));

            // Validación de Stock
            if (food.getStock() < itemDto.getQuantity()) {
                throw new InsufficientStockException("Stock insuficiente para: " + food.getName());
            }

            // Actualización de Stock
            food.setStock(food.getStock() - itemDto.getQuantity());
            foodRepository.save(food);

            // Creación del Item de Orden
            OrderItem orderItem = new OrderItem(
                    food.getId(),
                    food.getName(),
                    food.getPrice(),
                    itemDto.getQuantity()
            );
            orderItems.add(orderItem);
        }
        return orderItems;
    }
    // TÉCNICA DE REFACTORING: EXTRACT METHOD
    // Propósito: Separar el cálculo financiero de la lógica de inventario.
    private double calculateSubtotal(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
    }
    // TÉCNICA DE REFACTORING: EXTRACT METHOD
    // Propósito: Encapsular la lógica de validación de pago y cálculo de vuelto.
    private PaymentDetails processPayment(ketchupapp.ketchupbackend.dto.PaymentDetailsRequestDto paymentDto, double totalAmount) {
        if (paymentDto.getAmountPaid() < totalAmount) {
            throw new InsufficientPaymentException(
                    String.format("Monto de pago insuficiente. Total de la orden: %.2f, Monto pagado: %.2f",
                            totalAmount, paymentDto.getAmountPaid())
            );
        }

        double changeGiven = 0;
        if (paymentDto.getType() == PaymentType.CASH) {
            changeGiven = paymentDto.getAmountPaid() - totalAmount;
        }

        return new PaymentDetails(
                paymentDto.getType(),
                paymentDto.getAmountPaid(),
                changeGiven
        );
    }
}
