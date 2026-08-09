package com.autoprtz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

}