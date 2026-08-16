package com.autoprtz.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "account_transactions")
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================
    // TRANSACTION TYPE
    // =========================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountTransactionType type;


    // =========================
    // C/D NAME
    // =========================

    @Column(nullable = false)
    private String cdName;


    // =========================
    // ORDER NUMBER
    // =========================

    private String orderNumber;


    // =========================
    // AMOUNT
    // =========================

    @Column(
        nullable = false,
        precision = 15,
        scale = 2
    )
    private BigDecimal amount = BigDecimal.ZERO;


    // =========================
    // CREATED DATE
    // =========================

    @Column(nullable = false)
    private LocalDateTime createdAt =
            LocalDateTime.now();


    // =========================
    // GETTERS / SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public AccountTransactionType getType() {
        return type;
    }

    public void setType(
            AccountTransactionType type) {

        this.type = type;
    }


    public String getCdName() {
        return cdName;
    }

    public void setCdName(String cdName) {
        this.cdName = cdName;
    }


    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(
            String orderNumber) {

        this.orderNumber = orderNumber;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {

        this.amount = amount;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}