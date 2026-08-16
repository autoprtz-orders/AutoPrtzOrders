package com.autoprtz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.ExtraAmount;

public interface ExtraAmountRepository
        extends JpaRepository<ExtraAmount, Long> {

    List<ExtraAmount>
    findByNameContainingIgnoreCaseOrOrderNumberContainingIgnoreCase(
            String name,
            String orderNumber
    );
}