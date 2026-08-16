package com.autoprtz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.BankBalance;

public interface BankBalanceRepository
        extends JpaRepository<BankBalance, Long> {

    List<BankBalance>
    findByCdNameContainingIgnoreCaseOrOrderNumberContainingIgnoreCase(
            String cdName,
            String orderNumber
    );
}