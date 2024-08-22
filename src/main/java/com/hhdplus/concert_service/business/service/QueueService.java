package com.hhdplus.concert_service.business.service;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.QueueRepository;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueService {
    static Logger LOGGER = LoggerFactory.getLogger(QueueService.class);

    private final QueueRepository queueRepository;

    public QueueDomain createToken(UserDomain user) {
        QueueDomain queue = QueueDomain.builder()
                .userId(user.getUserId())
                .build();

        queue.create();

        try {
            queueRepository.save(queue);
            LOGGER.info("token create success: " + queue.getToken());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("token create error", e);
        }

        return queue;
    }

    public void verifyToken(String token) {
        if (!Optional.ofNullable(queueRepository.verifyToken(token)).orElse(false)) {
            throw new InvalidReqBodyException("INVALID_TOKEN");
        }
    }

    public long getQueueOrder(String token) {
        return Optional.ofNullable(queueRepository.getQueueOrder(token)).orElse(0L);
    }

    public void activateTokens() {
        queueRepository.activateTokens();
    }

    public void expireTokens() {
        queueRepository.expireTokens();
    }

    public void expireToken(String token) {
        queueRepository.expireToken(token);
    }
}
