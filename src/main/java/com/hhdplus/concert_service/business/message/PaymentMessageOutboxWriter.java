package com.hhdplus.concert_service.business.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hhdplus.concert_service.infrastructure.entity.PaymentOutbox;

import java.util.List;

public interface PaymentMessageOutboxWriter {
    PaymentOutbox save(PaymentMessage message) throws JsonProcessingException;
    PaymentOutbox complete(PaymentMessage message) throws JsonProcessingException;
    List<PaymentOutbox> findByStatus(String init);
}
