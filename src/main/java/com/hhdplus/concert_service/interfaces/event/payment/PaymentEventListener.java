package com.hhdplus.concert_service.interfaces.event.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hhdplus.concert_service.business.event.PaymentEvent;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import com.hhdplus.concert_service.business.message.PaymentMessageSender;
import com.hhdplus.concert_service.business.service.PaymentService;
import com.hhdplus.concert_service.infrastructure.entity.PaymentHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.ExecutionException;

@Component
public class PaymentEventListener {

    @Autowired
    private PaymentMessageOutboxWriter paymentMessageOutboxWriter;
    @Autowired
    private PaymentMessageSender paymentMessageSender;

    private final PaymentService paymentService;

    public PaymentEventListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void savePaymentHistoryHandler(PaymentHistory paymentsHistory) {
        // 결제 히스토리 저장
        paymentService.savePaymentHistory(PaymentHistory.toDomain(paymentsHistory));
    }

    // PaymentFacade의 paymentEventPublisher.createOutboxMessage(event) 실행.
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createOutboxMessage(PaymentEvent event) throws JsonProcessingException {
        PaymentMessage message = PaymentMessage.builder()
                .userId(event.getUserId())
                .price(event.getPrice())
                .status("INIT")
                .build();

        paymentMessageOutboxWriter.save(message);
    }

    // PaymentFacade의 sendMessage(PaymentEvent event) 실행
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessage(PaymentEvent event) throws JsonProcessingException, InterruptedException, ExecutionException {
        PaymentMessage message = PaymentMessage.builder()
                .id(event.getId())
                .userId(event.getUserId())
                .price(event.getPrice())
                .status("INIT")
                .build();

        paymentMessageSender.send(message);
    }
}
