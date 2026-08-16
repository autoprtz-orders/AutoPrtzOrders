package com.autoprtz.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.AccountUser;

public interface AccountUserRepository
        extends JpaRepository<AccountUser, Long> {

    Optional<AccountUser> findByUsername(String username);

    boolean existsByUsername(String username);
}