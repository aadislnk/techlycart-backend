package com.techlycart.backend.repository;

import com.techlycart.backend.entity.Cart;
import com.techlycart.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}
