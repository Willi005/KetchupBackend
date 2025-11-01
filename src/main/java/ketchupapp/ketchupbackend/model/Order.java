package ketchupapp.ketchupbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;

    private long ticketNumber;
    private String clientName;
    private List<OrderItem> items;
    private LocalDateTime orderTimestamp;
    private String employeeId;
    private String employeeName;
    private PaymentDetails payment;
    private double subtotal;
    private double totalAmount;

    private String kitchenNotes;
}
