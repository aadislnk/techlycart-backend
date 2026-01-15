package com.techlycart.backend.repository;

import com.techlycart.backend.entity.Order;
import com.techlycart.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}
