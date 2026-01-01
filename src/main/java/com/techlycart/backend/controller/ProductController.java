package com.techlycart.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techlycart.backend.entity.Product;
import com.techlycart.backend.repository.ProductRepository;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    //above is constructor injection
    /*
    sees ProductRepository
creates its implementation
injects it automatically
You didn’t write:
new ProductRepository()
This is Dependency Injection (we’ll formalize it later).*/

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    /*
    findAll()
productRepository.findAll();
This:
runs SELECT * FROM product
maps rows → Product objects
returns a List<Product>
All without SQL.
     */
}

