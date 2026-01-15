package com.techlycart.backend.dto;

import com.techlycart.backend.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private double totalAmount;
    private List<OrderItemResponse> items;

    public OrderResponse(
            Long orderId,
            LocalDateTime orderDate,
            OrderStatus status,
            double totalAmount,
            List<OrderItemResponse> items
    ) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
