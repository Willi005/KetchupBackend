package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.*;
import ketchupapp.ketchupbackend.exception.InsufficientPaymentException;
import ketchupapp.ketchupbackend.exception.InsufficientStockException;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.model.*;
import ketchupapp.ketchupbackend.repo.OrderRepository;
import ketchupapp.ketchupbackend.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InventoryService inventoryService; // Nuevo mock por el refactoring
    @Mock
    private PaymentService paymentService;     // Nuevo mock por el refactoring

    @InjectMocks
    private OrderServiceImpl orderService;

    private User employee;
    private OrderRequestDto orderRequest;
    private List<OrderItem> mockOrderItems;
    private PaymentDetails mockPaymentDetails;

    @BeforeEach
    void setUp() {
        // Configuración inicial
        employee = new User();
        employee.setId("emp-1");
        employee.setName("Juan");

        // Construcción básica del DTO de request
        orderRequest = new OrderRequestDto();
        orderRequest.setEmployeeId("emp-1");
        orderRequest.setClientName("Cliente Test");


        OrderItemRequestDto itemDto = new OrderItemRequestDto();
        itemDto.setFoodId("food-1");
        itemDto.setQuantity(2);
        orderRequest.setItems(List.of(itemDto));

        PaymentDetailsRequestDto paymentDto = new PaymentDetailsRequestDto();
        paymentDto.setType(PaymentType.CASH);
        paymentDto.setAmountPaid(10000.0);
        orderRequest.setPayment(paymentDto);

        // 3. Cofiguración de respuesta de los mocks
        // Simulamos que InventoryService devuelve una hamburguesa de 5,000 x 2 = 10,000 total
        mockOrderItems = List.of(
                new OrderItem("food-1", "Hamburguesa", 5000.0, 2)
        );

        // Simulamos que PaymentService devuelve un pago exacto
        mockPaymentDetails = new PaymentDetails(PaymentType.CASH, 10000.0, 0.0);
    }

    @Test
    @DisplayName("Debe crear orden exitosamente coordinando servicios")
    void createOrder_Success() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.of(employee));
        when(inventoryService.processOrderItems(any())).thenReturn(mockOrderItems);
        when(paymentService.processPayment(any(), eq(10000.0))).thenReturn(mockPaymentDetails);
        when(orderRepository.count()).thenReturn(100L);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId("new-id");
            return o;
        });

        // WHEN
        OrderResponseDto response = orderService.createOrder(orderRequest);

        // THEN
        assertNotNull(response);
        assertEquals(101L, response.ticketNumber());
        assertEquals(10000.0, response.totalAmount());
        verify(inventoryService).processOrderItems(any());
        verify(paymentService).processPayment(any(), eq(10000.0));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe fallar si el empleado no existe")
    void createOrder_EmployeeNotFound_ThrowsException() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(orderRequest));

        verifyNoInteractions(inventoryService);
        verifyNoInteractions(paymentService);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar si InventoryService lanza excepción de stock")
    void createOrder_InsufficientStock_ThrowsException() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.of(employee));
        // Simulamos que el servicio de inventario falla
        when(inventoryService.processOrderItems(any()))
                .thenThrow(new InsufficientStockException("Stock insuficiente"));

        // WHEN & THEN
        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(orderRequest));

        verifyNoInteractions(paymentService); // No debe intentar cobrar
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar si PaymentService lanza excepción de pago")
    void createOrder_InsufficientPayment_ThrowsException() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.of(employee));
        when(inventoryService.processOrderItems(any())).thenReturn(mockOrderItems);

        // Simulamos que el servicio de pago falla (por Ej: Pagó 5,000 pero costaba 10,000)
        when(paymentService.processPayment(any(), eq(10000.0)))
                .thenThrow(new InsufficientPaymentException("Dinero insuficiente"));

        // WHEN & THEN
        assertThrows(InsufficientPaymentException.class, () -> orderService.createOrder(orderRequest));

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe mapear correctamente el vuelto calculado por PaymentService")
    void createOrder_CalculateChange_Success() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.of(employee));
        when(inventoryService.processOrderItems(any())).thenReturn(mockOrderItems); // Total de 10,000

        // Configuración específica para este test:
        // El cliente paga 12,000
        orderRequest.getPayment().setAmountPaid(12000.0);

        // El servicio de pago debería devolver un objeto con 2,000 de vuelto.
        // Aquí se verifica que OrderService toma ese objeto y lo pone en la respuesta final.
        PaymentDetails paymentWithChange = new PaymentDetails(PaymentType.CASH, 12000.0, 2000.0);

        when(paymentService.processPayment(any(), eq(10000.0))).thenReturn(paymentWithChange);

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        OrderResponseDto response = orderService.createOrder(orderRequest);

        // THEN
        assertEquals(2000.0, response.payment().getChangeGiven());
        assertEquals(12000.0, response.payment().getAmountPaid());
    }
}