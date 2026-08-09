package com.autoprtz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autoprtz.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}