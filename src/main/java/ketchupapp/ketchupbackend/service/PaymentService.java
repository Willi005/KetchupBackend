package ketchupapp.ketchupbackend.service;

import ketchupapp.ketchupbackend.dto.PaymentDetailsRequestDto;
import ketchupapp.ketchupbackend.model.PaymentDetails;

public interface PaymentService {
    PaymentDetails processPayment(PaymentDetailsRequestDto paymentDto, double totalAmount);
}