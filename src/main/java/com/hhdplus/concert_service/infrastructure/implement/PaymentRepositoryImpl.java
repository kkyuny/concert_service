package com.hhdplus.concert_service.infrastructure.implement;

import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.repository.PaymentRepository;
import com.hhdplus.concert_service.infrastructure.entity.Payment;
import com.hhdplus.concert_service.infrastructure.repository.PaymentJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class PaymentRepositoryImpl implements PaymentRepository {

    @Autowired
    PaymentJpaRepository jpaRepository;

    @Override
    public void save(PaymentDomain domain) {
        jpaRepository.save(Payment.toEntity(domain));
    }
}
