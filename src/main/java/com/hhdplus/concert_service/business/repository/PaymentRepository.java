package com.hhdplus.concert_service.business.repository;

import com.hhdplus.concert_service.business.domain.PaymentDomain;

public interface PaymentRepository {
    PaymentDomain save(PaymentDomain domain);
}
