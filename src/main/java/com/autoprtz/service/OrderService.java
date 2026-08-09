package com.autoprtz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.autoprtz.entity.Order;
import com.autoprtz.entity.OrderProduct;
import com.autoprtz.repository.OrderRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Save Order with Products
     */
    public Order saveOrder(Order order) {

        if (order.getProducts() != null) {

            for (OrderProduct product : order.getProducts()) {

                product.setOrder(order);
            }
        }

        return orderRepository.save(order);
    }

    /**
     * Get Total Orders Count
     */
    public long getOrderCount() {

        return orderRepository.count();
    }
}