package com.autoprtz.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.autoprtz.entity.Order;
import com.autoprtz.entity.OrderNote;
import com.autoprtz.entity.OrderStatus;
import com.autoprtz.repository.OrderNoteRepository;
import com.autoprtz.repository.OrderRepository;
import com.autoprtz.service.OrderService;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderNoteRepository orderNoteRepository;


    // =========================
    // HOME PAGE
    // =========================

    @GetMapping("/")
    public String index() {
        return "index";
    }


    // =========================
    // DASHBOARD
    // =========================

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        long totalOrders = orderRepository.count();

        long processing =
                orderRepository.countByStatus(OrderStatus.PROCESSING);

        long sourced =
                orderRepository.countByStatus(OrderStatus.SOURCED);

        long hold =
                orderRepository.countByStatus(OrderStatus.HOLD);

        long pendingPayment =
                orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT);

        long completed =
                orderRepository.countByStatus(OrderStatus.COMPLETED);

        List<Order> recentOrders =
                orderRepository.findTop10ByOrderByIdDesc();


        model.addAttribute(
                "totalOrders",
                totalOrders
        );

        model.addAttribute(
                "processing",
                processing
        );

        model.addAttribute(
                "sourced",
                sourced
        );

        model.addAttribute(
                "hold",
                hold
        );

        model.addAttribute(
                "pendingPayment",
                pendingPayment
        );

        model.addAttribute(
                "completed",
                completed
        );

        model.addAttribute(
                "recentOrders",
                recentOrders
        );


        return "dashboard";
    }


    // =========================
    // PLACE ORDER PAGE
    // =========================

    @GetMapping("/place-order")
    public String placeOrder(Model model) {

        Order order = new Order();

        long count = orderRepository.count() + 1;

        String orderNumber =
                String.format("AP%06d", count);

        order.setOrderNumber(orderNumber);

        model.addAttribute(
                "order",
                order
        );

        return "place-order";
    }


    // =========================
    // SAVE ORDER
    // =========================

    @PostMapping("/saveOrder")
    public String saveOrder(
            Order order,
            Model model) {

        orderService.saveOrder(order);

        model.addAttribute(
                "successMessage",
                "Order Submitted Successfully! Order No : "
                        + order.getOrderNumber()
        );

        Order newOrder = new Order();

        long count =
                orderRepository.count() + 1;

        String orderNumber =
                String.format("AP%06d", count);

        newOrder.setOrderNumber(orderNumber);

        model.addAttribute(
                "order",
                newOrder
        );

        return "place-order";
    }


    // =========================
    // ORDERS LIST
    // SEARCH + STATUS FILTER
    // =========================

    @GetMapping("/orders")
    public String viewOrders(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            String status,

            Model model) {

        List<Order> orders;

        boolean hasSearch =
                search != null
                && !search.trim().isEmpty();

        boolean hasStatus =
                status != null
                && !status.trim().isEmpty()
                && !status.equalsIgnoreCase("ALL");


        // =========================
        // SEARCH
        // =========================

        if (hasSearch) {

            String searchValue =
                    search.trim();

            orders =
                    orderRepository
                    .findByOrderNumberContainingIgnoreCaseOrPhoneNumberContaining(
                            searchValue,
                            searchValue
                    );

        } else {

            orders =
                    orderRepository.findAll();
        }


        // =========================
        // STATUS FILTER
        // =========================

        if (hasStatus) {

            try {

                OrderStatus selectedStatus =
                        OrderStatus.valueOf(
                                status.toUpperCase()
                        );

                orders =
                        orders.stream()
                        .filter(order ->
                                order.getStatus() != null
                                && order.getStatus()
                                .equals(selectedStatus))
                        .toList();

            } catch (IllegalArgumentException e) {

                // Invalid status
                // Show all orders
            }
        }


        model.addAttribute(
                "orders",
                orders
        );

        model.addAttribute(
                "search",
                search != null
                        ? search
                        : ""
        );

        model.addAttribute(
                "selectedStatus",
                status != null
                        ? status
                        : "ALL"
        );

        model.addAttribute(
                "statuses",
                OrderStatus.values()
        );


        return "orders";
    }


    // =========================
    // VIEW SINGLE ORDER
    // =========================

    @GetMapping("/orders/{id}")
    public String viewOrder(
            @PathVariable Long id,
            Model model) {

        Order order =
                orderRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Order not found: " + id
                        )
                );

        model.addAttribute(
                "order",
                order
        );

        return "view-order";
    }


    // =========================
    // UPDATE ORDER
    // =========================

    @PostMapping("/orders/{id}/update")
    public String updateOrder(
            @PathVariable Long id,
            Order order,
            Model model) {

        Order existingOrder =
                orderRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Order not found: " + id
                        )
                );

        order.setId(
                existingOrder.getId()
        );

        orderService.saveOrder(order);

        Order updatedOrder =
                orderRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Order not found: " + id
                        )
                );

        model.addAttribute(
                "order",
                updatedOrder
        );

        model.addAttribute(
                "successMessage",
                "Order updated successfully!"
        );

        return "view-order";
    }


    // =========================
    // ADD NOTE
    // =========================

    @PostMapping("/orders/{id}/notes")
    public String addNote(
            @PathVariable Long id,
            @RequestParam("note")
            String note) {

        Order order =
                orderRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Order not found: " + id
                        )
                );


        if (note != null
                && !note.trim().isEmpty()) {

            OrderNote orderNote =
                    new OrderNote();

            orderNote.setNote(
                    note.trim()
            );

            orderNote.setCreatedAt(
                    LocalDateTime.now()
            );

            orderNote.setOrder(
                    order
            );

            orderNoteRepository.save(
                    orderNote
            );
        }


        return "redirect:/orders/" + id;
    }
}