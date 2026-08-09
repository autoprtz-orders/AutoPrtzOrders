package com.autoprtz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.Order;
import com.autoprtz.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Search by Order Number OR Phone Number
    List<Order> findByOrderNumberContainingIgnoreCaseOrPhoneNumberContaining(
            String orderNumber,
            String phoneNumber
    );

    // Dashboard - count orders by status
    long countByStatus(OrderStatus status);

    // Dashboard - latest 10 orders
    List<Order> findTop10ByOrderByIdDesc();
}