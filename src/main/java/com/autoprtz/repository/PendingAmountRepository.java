package com.autoprtz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.PendingAmount;

public interface PendingAmountRepository
        extends JpaRepository<PendingAmount, Long> {

    List<PendingAmount>
    findByNameContainingIgnoreCaseOrOrderNumberContainingIgnoreCase(
            String name,
            String orderNumber
    );
}