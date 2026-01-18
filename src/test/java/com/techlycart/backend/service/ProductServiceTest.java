package com.techlycart.backend.service;

import com.techlycart.backend.dto.CreateProductRequest;
import com.techlycart.backend.dto.ProductResponse;
import com.techlycart.backend.entity.Product;
import com.techlycart.backend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProductSuccessfully() {
        // given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Mouse");
        request.setDescription("Wireless mouse");
        request.setPrice(999);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Mouse");
        savedProduct.setDescription("Wireless mouse");
        savedProduct.setPrice(999);

        when(productRepository.save(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenReturn(savedProduct);

        // when
        ProductResponse response = productService.createProduct(request);

        // then
        assertThat(response.getName()).isEqualTo("Mouse");
        assertThat(response.getPrice()).isEqualTo(999);
    }
}
