package com.autoprtz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.autoprtz.entity.Order;
import com.autoprtz.repository.OrderRepository;
import com.autoprtz.service.OrderService;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/place-order")
    public String placeOrder(Model model) {

        Order order = new Order();

        long count = orderRepository.count() + 1;

        String orderNumber = String.format("AP%06d", count);

        order.setOrderNumber(orderNumber);

        model.addAttribute("order", order);

        return "place-order";
    }

    @PostMapping("/saveOrder")
    public String saveOrder(Order order, Model model) {

        orderService.saveOrder(order);

        model.addAttribute("successMessage",
                "Order Submitted Successfully! Order No : " + order.getOrderNumber());

        Order newOrder = new Order();

        long count = orderRepository.count() + 1;

        String orderNumber = String.format("AP%06d", count);

        newOrder.setOrderNumber(orderNumber);

        model.addAttribute("order", newOrder);

        return "place-order";
    }
    @GetMapping("/orders")
    public String viewOrders(Model model) {

        model.addAttribute("orders", orderRepository.findAll());

        return "orders";
    }
}