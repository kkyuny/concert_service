package com.hhdplus.concert_service.business.message;

public interface PaymentMessageSender {
    void send(PaymentMessage message);
}
