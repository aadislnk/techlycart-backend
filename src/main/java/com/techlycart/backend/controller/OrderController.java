package com.techlycart.backend.controller;

import com.techlycart.backend.dto.OrderResponse;
import com.techlycart.backend.entity.Order;
import com.techlycart.backend.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Place order from current cart
     */
    @PostMapping
    public OrderResponse placeOrder(Authentication authentication) {
        String username = authentication.getName();
        return orderService.placeOrder(username);
    }

    /**
     * Get logged-in user's orders
     */
    @GetMapping("/my")
    public List<OrderResponse> getMyOrders(Authentication authentication) {
        String username = authentication.getName();
        return orderService.getOrdersForUser(username);
    }

    /**
     * Admin: get all orders
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }
}