package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.PaymentDetailsRequestDto;
import ketchupapp.ketchupbackend.exception.InsufficientPaymentException;
import ketchupapp.ketchupbackend.model.PaymentDetails;
import ketchupapp.ketchupbackend.model.PaymentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentDetails processPayment(PaymentDetailsRequestDto paymentDto, double totalAmount) {
        log.debug("PAGO: Procesando pago de tipo '{}'. Total a pagar: ${}, Monto recibido: ${}",
                paymentDto.getType(), totalAmount, paymentDto.getAmountPaid());

        if (paymentDto.getAmountPaid() < totalAmount) {
            log.warn("PAGO: Intento fallido. Faltan ${}", totalAmount - paymentDto.getAmountPaid());
            throw new InsufficientPaymentException(
                    String.format("Monto de pago insuficiente. Total de la orden: %.2f, Monto pagado: %.2f",
                            totalAmount, paymentDto.getAmountPaid())
            );
        }

        double changeGiven = 0;
        if (paymentDto.getType() == PaymentType.CASH) {
            changeGiven = paymentDto.getAmountPaid() - totalAmount;
            log.debug("PAGO: Calculando vuelto en efectivo: ${}", changeGiven);
        }

        log.info("PAGO: Transacción aprobada. Vuelto generado: ${}", changeGiven);

        return new PaymentDetails(
                paymentDto.getType(),
                paymentDto.getAmountPaid(),
                changeGiven
        );
    }
}