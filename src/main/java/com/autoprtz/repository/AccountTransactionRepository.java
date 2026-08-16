package com.autoprtz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.AccountTransaction;

public interface AccountTransactionRepository
        extends JpaRepository<AccountTransaction, Long> {

    List<AccountTransaction>
    findAllByOrderByIdDesc();
}