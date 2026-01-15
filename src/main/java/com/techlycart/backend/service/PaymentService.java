package com.techlycart.backend.service;

import com.techlycart.backend.dto.PaymentResponse;
import com.techlycart.backend.entity.*;
import com.techlycart.backend.repository.OrderRepository;
import com.techlycart.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }
    @Transactional
    public PaymentResponse pay(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // idempotency
        Payment existingPayment =
                paymentRepository.findByOrder(order).orElse(null);

        if (existingPayment != null) {
            return mapToPaymentResponse(existingPayment);
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.INITIATED);

        boolean success = new Random().nextBoolean();

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PAID);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        return mapToPaymentResponse(payment);
    }

    public PaymentResponse getPaymentStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return mapToPaymentResponse(payment);
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentDate()
        );
    }


}
