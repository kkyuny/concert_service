package com.hhdplus.concert_service.infrastructure.repository;

import com.hhdplus.concert_service.infrastructure.entity.PaymentOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutbox, Long> {
    List<PaymentOutbox> findByStatus(String status);
}
