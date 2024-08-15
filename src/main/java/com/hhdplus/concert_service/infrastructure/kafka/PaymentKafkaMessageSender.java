package com.hhdplus.concert_service.infrastructure.kafka;

import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentKafkaMessageSender implements PaymentMessageSender {
    @Autowired
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(PaymentMessage message) {
        String PAYMENT_TOPIC = "Payment";
        kafkaTemplate.send(PAYMENT_TOPIC, message);
    }
}
