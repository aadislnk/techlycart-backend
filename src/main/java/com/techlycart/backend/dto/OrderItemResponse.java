package com.techlycart.backend.dto;

public class OrderItemResponse {

    private Long productId;
    private String productName;
    private double priceAtPurchase;
    private int quantity;

    public OrderItemResponse(
            Long productId,
            String productName,
            double priceAtPurchase,
            int quantity
    ) {
        this.productId = productId;
        this.productName = productName;
        this.priceAtPurchase = priceAtPurchase;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public int getQuantity() {
        return quantity;
    }
}
