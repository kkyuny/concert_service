package com.hhdplus.concert_service.business.message;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.ExecutionException;

public interface PaymentMessageSender {
    void send(PaymentMessage message) throws JsonProcessingException, InterruptedException, ExecutionException;
}
