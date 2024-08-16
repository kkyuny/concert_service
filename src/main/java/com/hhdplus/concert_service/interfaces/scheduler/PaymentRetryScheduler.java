package com.hhdplus.concert_service.interfaces.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import com.hhdplus.concert_service.business.message.PaymentMessageSender;
import com.hhdplus.concert_service.infrastructure.entity.PaymentOutbox;
import com.hhdplus.concert_service.infrastructure.repository.PaymentOutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentRetryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRetryScheduler.class);

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;
    @Autowired
    private PaymentMessageSender paymentMessageSender;
    @Autowired
    private PaymentMessageOutboxWriter paymentMessageOutboxWriter;
    @Autowired
    private ObjectMapper objectMapper;

    // outbox의 status가 init 상태인 메세지를 찾아 send 후 published로 status 변경.
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void retryPendingMessages() {
        List<PaymentOutbox> pendingMessages = paymentMessageOutboxWriter.findByStatus("INIT");

        for (PaymentOutbox message : pendingMessages) {
            try {
                // 메시지 전송
                PaymentMessage retryMessage = objectMapper.readValue(message.getMessage(), PaymentMessage.class);

                paymentMessageSender.send(retryMessage);

                // 성공적으로 전송되면 상태를 업데이트
                message.setStatus("PUBLISHED");

                paymentOutboxJpaRepository.save(message);
                logger.info("Successfully resent message with ID: " + message.getId());
            } catch (Exception e) {
                logger.error("Failed to resend message with ID: " + message.getId(), e);
            }
        }
    }
}
