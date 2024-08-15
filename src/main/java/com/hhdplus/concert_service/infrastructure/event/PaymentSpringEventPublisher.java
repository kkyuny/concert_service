package com.hhdplus.concert_service.infrastructure.event;

import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.event.PaymentEventPublisher;
import com.hhdplus.concert_service.infrastructure.entity.PaymentHistory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

// 이벤트 발행 서비스
@Component
public class PaymentSpringEventPublisher implements PaymentEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void savePaymentHistory(PaymentDomain paymentsHistory) {
        applicationEventPublisher.publishEvent(PaymentHistory.toEntity(paymentsHistory));
    }
}
