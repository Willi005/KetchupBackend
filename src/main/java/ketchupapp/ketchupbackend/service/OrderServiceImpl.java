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
    @Transactional // Esta etiqueta sirve para revertir tod/o en caso de que algo falle.
    public OrderResponseDto createOrder(OrderRequestDto dto) {

        // 1. Obtenemos el cajero (basado en el employeeId que se envía del frontend)
        User employee = userRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + dto.getEmployeeId()));

        List<OrderItem> orderItems = new ArrayList<>();
        double subtotal = 0;

        // 2. Se procesa cada item de la orden
        for (OrderItemRequestDto itemDto : dto.getItems()) {
            Food food = foodRepository.findById(itemDto.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comida no encontrada con id: " + itemDto.getFoodId()));

            // 2.1. Se valida el stock y si no hay stock se lanza una excepción personalizada.
            if (food.getStock() < itemDto.getQuantity()) {
                throw new InsufficientStockException("Stock insuficiente para: " + food.getName());
            }

            // 2.2. Se actualiza el stock
            food.setStock(food.getStock() - itemDto.getQuantity());
            foodRepository.save(food);

            // 2.3. Se crea el OrderItem con los precios del momento de venta.
            OrderItem orderItem = new OrderItem(
                    food.getId(),
                    food.getName(),
                    food.getPrice(),
                    itemDto.getQuantity()
            );
            orderItems.add(orderItem);

            // 2.4. Se cálcula el subtotal.
            subtotal += food.getPrice() * itemDto.getQuantity();
        }

        // 3. Se procesa el pago
        double totalAmount = subtotal; // Aquí se puede sumar IVA usando alguna const, pero por el momento está a discución.
        double changeGiven = 0;

        if (dto.getPayment().getAmountPaid() < totalAmount) {
            // Si el pago es menor que el total lanza la excepción.
            // Esto funciona para CASH y DEBIT_CARD.
            throw new InsufficientPaymentException(
                    String.format("Monto de pago insuficiente. Total de la orden: %.2f, Monto pagado: %.2f",
                            totalAmount, dto.getPayment().getAmountPaid())
            );
        }
        // Si la validación pasa calcula el vuelto (solo para CASH)
        if (dto.getPayment().getType() == PaymentType.CASH) {
            changeGiven = dto.getPayment().getAmountPaid() - totalAmount;
        }

        PaymentDetails paymentDetails = new PaymentDetails(
                dto.getPayment().getType(),
                dto.getPayment().getAmountPaid(),
                changeGiven
        );

        // 4. Se genera el número de ticket (sé que es mala práctica usar count() de Mongo pero es un proyecto de U. :p)
        long ticketNumber = orderRepository.count() + 1;

        // 5. Finalmente, se crea la orden.
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

        // 6. Se guarda la orden en la base de datos.
        Order savedOrder = orderRepository.save(order);

        // 7. Devolvemos el DTO con los datos de la orden.
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
}
