package com.hhdplus.concert_service.interfaces.consumer.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageConsumer {
    @Autowired
    ObjectMapper objectMapper;

    private final PaymentMessageOutboxWriter paymentMessageOutboxWriter;

    public PaymentMessageConsumer(PaymentMessageOutboxWriter paymentMessageOutboxWriter) {
        this.paymentMessageOutboxWriter = paymentMessageOutboxWriter;
    }

    // kafka에서 메세지 send 시 실행.
    @KafkaListener(topics = "Payment", groupId = "group_1")
    void complete(String message) throws JsonProcessingException {
        PaymentMessage paymentMessage = objectMapper.readValue(message, PaymentMessage.class);

        paymentMessageOutboxWriter.complete(paymentMessage);
    }
}
