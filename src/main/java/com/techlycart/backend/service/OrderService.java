package com.techlycart.backend.service;

import com.techlycart.backend.dto.OrderItemResponse;
import com.techlycart.backend.dto.OrderResponse;
import com.techlycart.backend.entity.*;
import com.techlycart.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    public OrderResponse placeOrder(String username) {

        // 1. Fetch user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Fetch cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3. Create Order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);

        double totalAmount = 0.0;

        // 4. Convert CartItems → OrderItems
        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);

            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            totalAmount +=
                    cartItem.getProduct().getPrice() * cartItem.getQuantity();

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(totalAmount);

        // 5. Save order (cascade saves order items)
        Order savedOrder = orderRepository.save(order);

        // 6. Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponse(savedOrder);

    }
    public List<OrderResponse> getOrdersForUser(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPriceAtPurchase(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount(),
                items
        );
    }


}