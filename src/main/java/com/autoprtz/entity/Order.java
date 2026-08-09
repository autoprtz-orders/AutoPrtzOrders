package com.autoprtz.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String location;

    @Column(length = 150)
    private String email;

    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    private String partDetails;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private OrderStatus status = OrderStatus.PROCESSING;

    @Column(length = 200)
    private String vendor;

    @Column(length = 200)
    private String sourcingPerson;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    /*
     * Old single-note field.
     * Kept temporarily so existing database data is not disturbed.
     */
    @Column(length = 2000)
    private String note;

    /*
     * Multiple order notes.
     * Every note is stored separately in order_notes table.
     */
    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderNote> notes = new ArrayList<>();

    /*
     * Order products.
     */
    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderProduct> products = new ArrayList<>();


    public Order() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }


    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getPartDetails() {
        return partDetails;
    }

    public void setPartDetails(String partDetails) {
        this.partDetails = partDetails;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }


    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }


    public String getSourcingPerson() {
        return sourcingPerson;
    }

    public void setSourcingPerson(String sourcingPerson) {
        this.sourcingPerson = sourcingPerson;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public List<OrderNote> getNotes() {
        return notes;
    }

    public void setNotes(List<OrderNote> notes) {
        this.notes = notes;
    }


    public List<OrderProduct> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProduct> products) {
        this.products = products;
    }
}