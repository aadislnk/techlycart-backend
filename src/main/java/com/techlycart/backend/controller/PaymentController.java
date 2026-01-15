package com.techlycart.backend.controller;

import com.techlycart.backend.dto.PaymentResponse;
import com.techlycart.backend.entity.Payment;
import com.techlycart.backend.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    //Initiate payment for an order
    @PostMapping("/pay/{orderId}")
    public PaymentResponse pay(@PathVariable Long orderId) {
        return paymentService.pay(orderId);
    }

    //Get payment stats for an order
    @GetMapping("/status/{orderId}")
    public PaymentResponse getPaymentStatus(@PathVariable Long orderId) {
        return paymentService.getPaymentStatus(orderId);
    }
}
