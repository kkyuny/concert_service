package com.hhdplus.concert_service.business.tempSender;

import com.hhdplus.concert_service.business.message.PaymentMessage;

public interface ExternalSender {
    void sendPaymentResult(PaymentMessage message);
}
