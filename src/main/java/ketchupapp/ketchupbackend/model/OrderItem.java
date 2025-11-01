package ketchupapp.ketchupbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String foodId;
    private String name;
    private double priceAtPurchase; // Precio al momento de la venta
    private int quantity;
}