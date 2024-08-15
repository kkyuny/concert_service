package com.hhdplus.concert_service.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class PaymentKafkaMessageSender implements PaymentMessageSender {
    @Autowired
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    // PaymentEventListener -> sendMessage(PaymentEvent event) 실행.
    @Override
    public void send(PaymentMessage message) throws JsonProcessingException, InterruptedException, ExecutionException {
        String PAYMENT_TOPIC = "Payment";
        String jsonMessage = objectMapper.writeValueAsString(message);
        kafkaTemplate.send(PAYMENT_TOPIC, jsonMessage).get();
    }
}
