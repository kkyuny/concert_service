package com.hhdplus.concert_service.business.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hhdplus.concert_service.infrastructure.entity.PaymentOutbox;

import java.util.List;

public interface PaymentMessageOutboxWriter {
    void save(PaymentMessage message) throws JsonProcessingException;
    void complete(PaymentMessage message);
    List<PaymentOutbox> findByStatus(String init);
}
