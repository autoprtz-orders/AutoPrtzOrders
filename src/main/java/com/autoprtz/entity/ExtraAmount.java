package com.autoprtz.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "extra_amount")
public class ExtraAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String orderNumber;

    private BigDecimal haveAmount = BigDecimal.ZERO;

    private BigDecimal giveAmount = BigDecimal.ZERO;

    private LocalDate transactionDate;


    public ExtraAmount() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


    public BigDecimal getHaveAmount() {
        return haveAmount;
    }

    public void setHaveAmount(BigDecimal haveAmount) {
        this.haveAmount = haveAmount;
    }


    public BigDecimal getGiveAmount() {
        return giveAmount;
    }

    public void setGiveAmount(BigDecimal giveAmount) {
        this.giveAmount = giveAmount;
    }


    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }


    public BigDecimal getTotal() {

        BigDecimal have =
                haveAmount != null
                        ? haveAmount
                        : BigDecimal.ZERO;

        BigDecimal give =
                giveAmount != null
                        ? giveAmount
                        : BigDecimal.ZERO;

        return have.subtract(give);
    }
}