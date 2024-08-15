package com.hhdplus.concert_service.infrastructure.implement;

import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.repository.PaymentRepository;
import com.hhdplus.concert_service.infrastructure.entity.Payment;
import com.hhdplus.concert_service.infrastructure.repository.PaymentHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentHistoryRepositoryImpl implements PaymentRepository {

    private final PaymentHistoryJpaRepository jpaRepository;

    @Override
    public PaymentDomain save(PaymentDomain domain) {
        return Payment.toDomain(jpaRepository.save(Payment.toEntity(domain)));
    }
}
