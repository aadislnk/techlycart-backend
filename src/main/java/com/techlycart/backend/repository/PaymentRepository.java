package com.techlycart.backend.repository;

import com.techlycart.backend.entity.Payment;
import com.techlycart.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);
}
