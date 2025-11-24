package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.OrderItemRequestDto;
import ketchupapp.ketchupbackend.dto.OrderRequestDto;
import ketchupapp.ketchupbackend.dto.OrderResponseDto;
import ketchupapp.ketchupbackend.dto.PaymentDetailsRequestDto;
import ketchupapp.ketchupbackend.exception.InsufficientPaymentException;
import ketchupapp.ketchupbackend.exception.InsufficientStockException;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.model.*;
import ketchupapp.ketchupbackend.repo.FoodRepository;
import ketchupapp.ketchupbackend.repo.OrderRepository;
import ketchupapp.ketchupbackend.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User employee;
    private Food foodItem;
    private OrderRequestDto orderRequest;

    @BeforeEach
    void setUp() {
        // Configuración inicial
        employee = new User();
        employee.setId("emp-1");
        employee.setName("Juan");
        employee.setUsername("juanperez");

        foodItem = new Food();
        foodItem.setId("food-1");
        foodItem.setName("Hamburguesa");
        foodItem.setPrice(5000.0);
        foodItem.setStock(10);
        foodItem.setCategory(FoodCategory.BURGER);

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
        paymentDto.setAmountPaid(10000.0); // Exacto: 5000 * 2
        orderRequest.setPayment(paymentDto);
    }

    @Test
    @DisplayName("Debe crear orden exitosamente y descontar stock")
    void createOrder_Success() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.of(employee));
        when(foodRepository.findById("food-1")).thenReturn(Optional.of(foodItem));
        when(orderRepository.count()).thenReturn(100L); // Simulamos que hay 100 órdenes previas

        // Simulamos que al guardar retorna una orden con ID
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId("order-new-1");
            return savedOrder;
        });

        // WHEN
        OrderResponseDto response = orderService.createOrder(orderRequest);

        // THEN
        assertNotNull(response);
        assertEquals(101L, response.ticketNumber()); // 100 + 1
        assertEquals(10000.0, response.totalAmount());
        assertEquals("Cliente Test", response.clientName());

        // Verificar que se actualizó el stock: Tenía 10, compro 2 -> Quedan 8
        assertEquals(8, foodItem.getStock());
        verify(foodRepository).save(foodItem); // Verifica que se guardó la comida actualizada
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe fallar si el empleado no existe")
    void createOrder_EmployeeNotFound_ThrowsException() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        // Asegurar que no se tocó el repositorio de órdenes ni comida
        verify(foodRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar si no hay stock suficiente")
    void createOrder_InsufficientStock_ThrowsException() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.of(employee));
        when(foodRepository.findById("food-1")).thenReturn(Optional.of(foodItem));

        // Modificamos el request para pedir más de lo que hay (actualmente el stock = 10)
        orderRequest.getItems().get(0).setQuantity(15);

        // WHEN & THEN
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(orderRepository, never()).save(any()); // No se debe crear orden
    }

    @Test
    @DisplayName("Debe fallar si el pago es insuficiente")
    void createOrder_InsufficientPayment_ThrowsException() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.of(employee));
        when(foodRepository.findById("food-1")).thenReturn(Optional.of(foodItem));

        // Costo total es 10,000 (5000 * 2), pagamos solo 5000
        orderRequest.getPayment().setAmountPaid(5000.0);

        // WHEN & THEN
        InsufficientPaymentException exception = assertThrows(InsufficientPaymentException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("Monto de pago insuficiente"));
        verify(foodRepository).save(foodItem);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe calcular el vuelto correctamente (CASH)")
    void createOrder_CalculateChange_Success() {
        // GIVEN
        when(userRepository.findById("emp-1")).thenReturn(Optional.of(employee));
        when(foodRepository.findById("food-1")).thenReturn(Optional.of(foodItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Costo: 10,000. Pago: 12,000
        orderRequest.getPayment().setAmountPaid(12000.0);

        // WHEN
        OrderResponseDto response = orderService.createOrder(orderRequest);

        // THEN
        assertEquals(2000.0, response.payment().getChangeGiven());
    }
}