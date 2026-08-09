package com.autoprtz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderNumberContainingIgnoreCaseOrPhoneNumberContaining(
            String orderNumber,
            String phoneNumber
    );

}