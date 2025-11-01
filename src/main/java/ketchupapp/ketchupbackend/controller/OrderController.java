package ketchupapp.ketchupbackend.controller;

import ketchupapp.ketchupbackend.dto.OrderRequestDto;
import ketchupapp.ketchupbackend.dto.OrderResponseDto;
import ketchupapp.ketchupbackend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins="http://localhost:5173")
public class OrderController {

    @Autowired
    @Qualifier("orderService")
    private OrderService orderService;

    // POST /orders
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest) {
        OrderResponseDto newOrder = orderService.createOrder(orderRequest);
        // Devuelve 201 Created y la orden completa (solo para pruebas de momento).
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    // GET /orders
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // GET /orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable String id) {
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
}