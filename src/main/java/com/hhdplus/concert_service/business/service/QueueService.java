package com.hhdplus.concert_service.business.service;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.QueueRepository;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import com.hhdplus.concert_service.interfaces.dto.request.QueueRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class QueueService {
    private static final int MAX_ACTIVE_USERS = 100;
    private static final int MAX_ACTIVE_MINUTES = 5;

    static Logger LOGGER = LoggerFactory.getLogger(QueueService.class);

    @Autowired
    QueueRepository queueRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public QueueDomain createToken(UserDomain user) {

        QueueDomain queue = QueueDomain.builder()
                .userId(user.getUserId())
                .build();

        queue.create();

        try{
            return queueRepository.save(queue);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("token create error", e);
            // 공통 값 추가해서 queue에 error값 set 필요
        }

        return queue;
    }

    public QueueDomain findTokenById(String token) {
        return queueRepository.findById(token)
                .orElseThrow(() -> new InvalidReqBodyException("INVALID_TOKEN"));
    }

    public QueueDomain getActiveUserCount(QueueDomain queue) {
        QueueDomain.builder()
                .queueCount((long)queueRepository.findActiveQueues(queue.getNo()).size())
                .build();

        return queue;
    }

    public boolean checkQueue(QueueDomain queue) {
        long activeUsersCount = getActiveUserCount(queue).getQueueCount();

        return activeUsersCount >= MAX_ACTIVE_USERS;
    }

    public QueueDomain changeStatusToActive(QueueDomain queue) {
        QueueDomain.builder()
            .status("active")
            .validDate(LocalDateTime.now().plusMinutes(MAX_ACTIVE_MINUTES))
            .build();

        return queueRepository.save(queue);
    }
}
