package com.hhdplus.concert_service.interfaces.consumer.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import com.hhdplus.concert_service.business.tempSender.ExternalSender;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMessageConsumer {
    @Autowired
    ObjectMapper objectMapper;

    private final PaymentMessageOutboxWriter paymentMessageOutboxWriter;

    @KafkaListener(topics = "Payment", groupId = "group_1")
    void processPaymentMessage(String message) throws JsonProcessingException {
        PaymentMessage paymentMessage = objectMapper.readValue(message, PaymentMessage.class);

        // Outbox 업데이트
        paymentMessageOutboxWriter.complete(paymentMessage);

        // 외부 발송 처리
        ExternalSender.sendPaymentResult(paymentMessage);
    }
}
