package com.autoprtz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoprtz.entity.OrderNote;

public interface OrderNoteRepository extends JpaRepository<OrderNote, Long> {

    List<OrderNote> findByOrderIdOrderByCreatedAtDesc(Long orderId);

}