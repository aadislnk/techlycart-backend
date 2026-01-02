package com.techlycart.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

//No id
//No DB annotations
//Client cannot control internal fields
public class CreateProductRequest {

    @NotBlank //validation
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @Positive
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
