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

        Payment payment = paymentRepository.findByOrder(order).orElse(null);

        // Case 1: Already paid → idempotent
        if (payment != null && payment.getStatus() == PaymentStatus.SUCCESS) {
            return mapToPaymentResponse(payment);
        }

        // Case 2: Failed earlier → retry allowed with 90% success rate
        if (payment != null && payment.getStatus() == PaymentStatus.FAILED) {
            boolean success = new Random().nextInt(100) < 90; // 90% success

            if (success) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaymentDate(LocalDateTime.now());
                order.setStatus(OrderStatus.PAID);
            } else {
                payment.setPaymentDate(LocalDateTime.now());
                // Keep status as FAILED
            }

            paymentRepository.save(payment);
            orderRepository.save(order);

            return mapToPaymentResponse(payment);
        }

        // Case 3: First payment attempt - 90% success rate
        boolean success = new Random().nextInt(100) < 90; // 90% success

        Payment newPayment = new Payment();
        newPayment.setOrder(order);
        newPayment.setAmount(order.getTotalAmount());
        newPayment.setPaymentDate(LocalDateTime.now());

        if (success) {
            newPayment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PAID);
        } else {
            newPayment.setStatus(PaymentStatus.FAILED);
            // Order status remains PLACED
        }

        paymentRepository.save(newPayment);
        orderRepository.save(order);

        return mapToPaymentResponse(newPayment);
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