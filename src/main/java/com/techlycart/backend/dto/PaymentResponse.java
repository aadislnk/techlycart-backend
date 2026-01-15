package com.techlycart.backend.dto;

import com.techlycart.backend.entity.PaymentStatus;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long paymentId;
    private Long orderId;
    private double amount;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

    public PaymentResponse(
            Long paymentId,
            Long orderId,
            double amount,
            PaymentStatus status,
            LocalDateTime paymentDate
    ) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.paymentDate = paymentDate;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
}
