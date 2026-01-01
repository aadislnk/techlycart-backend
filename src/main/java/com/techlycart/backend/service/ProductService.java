package com.techlycart.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.techlycart.backend.entity.Product;
import com.techlycart.backend.repository.ProductRepository;

@Service
public class  ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

}
