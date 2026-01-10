package com.techlycart.backend.dto;

public class CartItemResponse {

    private Long productId;
    private String productName;
    private double price;
    private int quantity;

    public CartItemResponse(
            Long productId,
            String productName,
            double price,
            int quantity
    ) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
