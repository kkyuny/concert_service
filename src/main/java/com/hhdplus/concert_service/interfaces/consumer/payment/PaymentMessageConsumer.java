package com.hhdplus.concert_service.interfaces.consumer.payment;

import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageConsumer {
    private final PaymentMessageOutboxWriter paymentMessageOutboxWriter;

    public PaymentMessageConsumer(PaymentMessageOutboxWriter paymentMessageOutboxWriter) {
        this.paymentMessageOutboxWriter = paymentMessageOutboxWriter;
    }

    @KafkaListener(topics = "Payment", groupId = "group_1")
    void complete(PaymentMessage message) {
        paymentMessageOutboxWriter.complete(message);
    }
}
