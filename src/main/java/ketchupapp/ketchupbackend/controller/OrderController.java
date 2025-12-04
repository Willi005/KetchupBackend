package ketchupapp.ketchupbackend.controller;

import ketchupapp.ketchupbackend.dto.OrderRequestDto;
import ketchupapp.ketchupbackend.dto.OrderResponseDto;
import ketchupapp.ketchupbackend.service.OrderService;
import ketchupapp.ketchupbackend.model.Order;
import ketchupapp.ketchupbackend.repo.OrderRepository;
import ketchupapp.ketchupbackend.exception.ResourceNotFoundException;
import ketchupapp.ketchupbackend.service.TicketService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins="http://localhost:5173")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository; // Acceso directo para el PDF (simplificación)
    private final TicketService ticketService;

    public OrderController(@Qualifier("orderService") OrderService orderService,
                           OrderRepository orderRepository,
                           TicketService ticketService) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.ticketService = ticketService;
    }

    // POST /orders
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest) {
        OrderResponseDto newOrder = orderService.createOrder(orderRequest);
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

    // NUEVO: GET /orders/{id}/ticket
    @GetMapping("/{id}/ticket")
    public ResponseEntity<byte[]> getOrderTicket(@PathVariable String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));

        byte[] pdfBytes = ticketService.generateTicket(order);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // "inline" hace que se abra en el navegador en lugar de descargar forzosamente
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ticket_" + order.getTicketNumber() + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}