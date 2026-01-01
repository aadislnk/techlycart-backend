package com.techlycart.backend.dto;
//No id
//No DB annotations
//Client cannot control internal fields
public class CreateProductRequest {

    private String name;
    private String description;
    private double price;

    // getters & setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
