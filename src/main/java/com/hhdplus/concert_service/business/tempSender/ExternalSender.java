package com.hhdplus.concert_service.business.tempSender;

import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.service.QueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ExternalSender {
    static Logger LOGGER = LoggerFactory.getLogger(QueueService.class);

    static void sendPaymentResult(PaymentMessage message) {
        LOGGER.info("send external message");
    }
}
