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
    // DASHBOARD / HOME PAGE
    // =========================

    @GetMapping("/")
    public String dashboard(Model model) {

        List<Order> orders = orderRepository.findAll();

        long totalOrders = orders.size();

        long processingCount = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PROCESSING)
                .count();

        long sourcedCount = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.SOURCED)
                .count();

        long holdCount = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.HOLD)
                .count();

        long pendingPaymentCount = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING_PAYMENT)
                .count();

        long completedCount = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .count();


        // Recent 5 orders
        List<Order> recentOrders = orders.stream()
                .sorted((a, b) -> {

                    if (a.getId() == null && b.getId() == null) {
                        return 0;
                    }

                    if (a.getId() == null) {
                        return 1;
                    }

                    if (b.getId() == null) {
                        return -1;
                    }

                    return Long.compare(
                            b.getId(),
                            a.getId()
                    );
                })
                .limit(5)
                .toList();


        model.addAttribute(
                "totalOrders",
                totalOrders
        );

        model.addAttribute(
                "processingCount",
                processingCount
        );

        model.addAttribute(
                "sourcedCount",
                sourcedCount
        );

        model.addAttribute(
                "holdCount",
                holdCount
        );

        model.addAttribute(
                "pendingPaymentCount",
                pendingPaymentCount
        );

        model.addAttribute(
                "completedCount",
                completedCount
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
                String.format(
                        "AP%06d",
                        count
                );

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
                String.format(
                        "AP%06d",
                        count
                );

        newOrder.setOrderNumber(
                orderNumber
        );

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
            @RequestParam(
                    required = false
            )
            String search,

            @RequestParam(
                    required = false
            )
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
        // ORDER NUMBER OR PHONE
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
                                &&
                                order.getStatus()
                                .equals(selectedStatus)
                        )
                        .toList();


            } catch (
                    IllegalArgumentException e) {

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
                                "Order not found: "
                                + id
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
                                "Order not found: "
                                + id
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
                                "Order not found: "
                                + id
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
                                "Order not found: "
                                + id
                        )
                );


        if (
                note != null
                &&
                !note.trim().isEmpty()
        ) {


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