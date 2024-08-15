package com.hhdplus.concert_service.business.event;

import com.hhdplus.concert_service.business.domain.PaymentDomain;

public interface PaymentEventPublisher {
    void savePaymentHistory(PaymentDomain paymentsHistory);
}
