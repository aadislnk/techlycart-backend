package com.techlycart.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.techlycart.backend.entity.Product;
import com.techlycart.backend.repository.ProductRepository;

import com.techlycart.backend.dto.CreateProductRequest;
import com.techlycart.backend.dto.ProductResponse;

@Service
public class  ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    //DTO ---->entity conversion:
//    public Product createProduct(CreateProductRequest request) {
//
//        Product product = new Product();
//        product.setName(request.getName());
//        product.setDescription(request.getDescription());
//        product.setPrice(request.getPrice());
//
//        return productRepository.save(product);
//    } //-----> Yes, it’s manual.
    //Yes, it’s repetitive.
    //That’s intentional,you must understand mapping before automating.

    //entity------->DTO conversion:
    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());

        return response;
    }
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product(); //rows
        product.setName(request.getName()); //cols
        product.setDescription(request.getDescription()); //cols
        product.setPrice(request.getPrice()); //cols

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }


}
