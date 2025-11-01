package ketchupapp.ketchupbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {
    private PaymentType type;
    private double amountPaid; // Lo que pagó el cliente en el momento de la venta
    private double changeGiven; // El vuelto de la venta :)
}