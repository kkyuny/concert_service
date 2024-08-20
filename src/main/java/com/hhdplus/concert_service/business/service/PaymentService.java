package com.hhdplus.concert_service.business.service;

import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.repository.PaymentHistoryRepository;
import com.hhdplus.concert_service.business.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
public class PaymentService {

    static Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    private final PaymentHistoryRepository paymentHistoryRepository;

    public PaymentDomain savePayment(PaymentDomain domain){
        try {
            return paymentRepository.save(domain);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("Payment execute error", e);

            return null;
        }
    }

    // event listener 에 의한 결제 히스토리 저장
    public void savePaymentHistory(PaymentDomain domain) {
        try {
            paymentHistoryRepository.save(domain);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("Payment history save error", e);
        }
    }
}
