package com.hhdplus.concert_service.business.event;

import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.message.PaymentMessage;

public interface PaymentEventPublisher {
    void savePaymentHistory(PaymentDomain paymentsHistory);

    void createOutboxMessage(PaymentEvent message);

    void sendEvent(PaymentEvent event);
}
