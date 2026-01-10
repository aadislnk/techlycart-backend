package com.techlycart.backend.dto;

import java.util.List;

public class CartResponse {

    private List<CartItemResponse> items;
    private int totalItems;
    private double totalPrice;

    public CartResponse(
            List<CartItemResponse> items,
            int totalItems,
            double totalPrice
    ) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
