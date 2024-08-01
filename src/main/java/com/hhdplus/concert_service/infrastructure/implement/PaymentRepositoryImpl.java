package com.hhdplus.concert_service.infrastructure.implement;

import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.repository.PaymentRepository;
import com.hhdplus.concert_service.infrastructure.entity.Payment;
import com.hhdplus.concert_service.infrastructure.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    @Override
    public PaymentDomain save(PaymentDomain domain) {
        return Payment.toDomain(jpaRepository.save(Payment.toEntity(domain)));
    }
}
