package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.PaymentDetailsRequestDto;
import ketchupapp.ketchupbackend.exception.InsufficientPaymentException;
import ketchupapp.ketchupbackend.model.PaymentDetails;
import ketchupapp.ketchupbackend.model.PaymentType;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentDetails processPayment(PaymentDetailsRequestDto paymentDto, double totalAmount) {
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