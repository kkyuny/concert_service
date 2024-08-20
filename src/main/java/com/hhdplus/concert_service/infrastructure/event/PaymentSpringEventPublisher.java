package com.hhdplus.concert_service.infrastructure.event;

import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.event.PaymentEvent;
import com.hhdplus.concert_service.business.event.PaymentEventPublisher;
import com.hhdplus.concert_service.infrastructure.entity.PaymentHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

// 이벤트 발행 서비스
@Component
@RequiredArgsConstructor
public class PaymentSpringEventPublisher implements PaymentEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void savePaymentHistory(PaymentDomain paymentsHistory) {
        applicationEventPublisher.publishEvent(PaymentHistory.toEntity(paymentsHistory));
    }

    @Override
    public void createOutboxMessage(PaymentEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void sendEvent(PaymentEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
