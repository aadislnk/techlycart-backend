package com.techlycart.backend.controller;

import java.util.List;

import com.techlycart.backend.dto.CreateProductRequest;
import com.techlycart.backend.dto.ProductResponse;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.techlycart.backend.entity.Product;
import com.techlycart.backend.service.ProductService;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

//    @GetMapping
//    public List<Product> getAllProducts() {
//        return productService.getAllProducts();
//    }
//    @PostMapping
//    public Product createProduct(@RequestBody Product product) {
//        return productService.createProduct(product);
//    }

    @PostMapping
    public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

//    @GetMapping
//    public List<ProductResponse> getAllProducts() {
//        return productService.getAllProducts();
//    }

//    @GetMapping
//    public Page<ProductResponse> getProducts(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size) {
//
//        return productService.getProducts(page, size);
//    }
    @GetMapping
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        if (search != null && !search.isBlank()) {
            return productService.searchProducts(search, page, size);
        }

        return productService.getProducts(page, size);
    }



}


