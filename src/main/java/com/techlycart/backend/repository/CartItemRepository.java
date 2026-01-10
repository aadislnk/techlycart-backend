package com.techlycart.backend.repository;

import com.techlycart.backend.entity.Cart;
import com.techlycart.backend.entity.CartItem;
import com.techlycart.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
