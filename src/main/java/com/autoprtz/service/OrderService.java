package com.autoprtz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autoprtz.entity.Order;
import com.autoprtz.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Save Order
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * Get Total Orders Count
     */
    public long getOrderCount() {
        return orderRepository.count();
    }
}