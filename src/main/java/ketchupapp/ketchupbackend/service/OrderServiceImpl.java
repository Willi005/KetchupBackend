package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.OrderRequestDto;
import ketchupapp.ketchupbackend.dto.OrderResponseDto;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.model.Order;
import ketchupapp.ketchupbackend.model.OrderItem;
import ketchupapp.ketchupbackend.model.PaymentDetails;
import ketchupapp.ketchupbackend.model.User;
import ketchupapp.ketchupbackend.repo.OrderRepository;
import ketchupapp.ketchupbackend.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("orderService")
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    // Nuevas dependencias
    private final InventoryService inventoryService;
    private final PaymentService paymentService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            InventoryService inventoryService,
                            PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto) {
        // Log informativo al intentar iniciar una venta
        log.info("INICIANDO VENTA: Cliente '{}' atendido por Cajero ID '{}'", dto.getClientName(), dto.getEmployeeId());

        try {
            // 1. Obtenemos el cajero
            User employee = userRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> {
                        // Log de error si no encontramos al empleado
                        log.error("ERROR VENTA: Cajero con ID '{}' no existe", dto.getEmployeeId());
                        return new ResourceNotFoundException("Empleado no encontrado con id: " + dto.getEmployeeId());
                    });

            // 2. Delegamos la gestión de items a InventoryService
            List<OrderItem> orderItems = inventoryService.processOrderItems(dto.getItems());

            // 3. Calculamos el subtotal
            double subtotal = calculateSubtotal(orderItems);
            double totalAmount = subtotal;

            // 4. Delegamos la gestión de pagos a PaymentService
            PaymentDetails paymentDetails = paymentService.processPayment(dto.getPayment(), totalAmount);

            // 5. Generamos el ticket y guardamos
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

            // Log de éxito con los detalles financieros de la transacción
            log.info("VENTA EXITOSA [Ticket #{}]: Total ${} | Medio de Pago: {} | Items: {}",
                    savedOrder.getTicketNumber(),
                    savedOrder.getTotalAmount(),
                    savedOrder.getPayment().getType(),
                    savedOrder.getItems().size());

            return mapToResponseDto(savedOrder);

        } catch (Exception e) {
            // Log de error general para capturar problemas de stock, pago o base de datos
            log.error("FALLO EN VENTA: No se pudo completar la orden del cliente '{}'. Causa: {}",
                    dto.getClientName(), e.getMessage());
            throw e; // Relanzamos la excepción para que el controlador la maneje como siempre
        }
    }

    private double calculateSubtotal(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
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