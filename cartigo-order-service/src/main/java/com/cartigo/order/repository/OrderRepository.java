package com.cartigo.order.repository;

import com.cartigo.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findByUserId(Long userId);
}