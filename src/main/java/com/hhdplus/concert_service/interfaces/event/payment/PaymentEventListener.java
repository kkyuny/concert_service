package com.hhdplus.concert_service.interfaces.event.payment;

import com.hhdplus.concert_service.business.service.PaymentService;
import com.hhdplus.concert_service.infrastructure.entity.PaymentHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEventListener {

    @Autowired
    private PaymentService paymentsService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void savePaymentHistoryHandler(PaymentHistory paymentsHistory) {
        // 결제 히스토리 저장
        paymentsService.savePaymentHistory(PaymentHistory.toDomain(paymentsHistory));
    }
}
