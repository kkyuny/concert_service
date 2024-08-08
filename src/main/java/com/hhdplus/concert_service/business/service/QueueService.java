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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueService {
    private static final int MAX_ACTIVE_USERS = 100;
    private static final int MAX_ACTIVE_MINUTES = 5;

    static Logger LOGGER = LoggerFactory.getLogger(QueueService.class);

    private final QueueRepository queueRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public QueueDomain createToken(UserDomain user) {

        QueueDomain queue = QueueDomain.builder()
                .userId(user.getUserId())
                .build();

        queue.create();

        try {
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
        List<QueueDomain> activeQueues = queueRepository.findActiveQueues(queue.getToken());

        Long activeCount = (long) activeQueues.size();

        return QueueDomain.builder()
                .queueCount(activeCount)
                .build();
    }

    public QueueDomain getActiveUserCount() {
        List<QueueDomain> activeQueues = queueRepository.findActiveQueues();

        Long activeCount = (long) activeQueues.size();

        return QueueDomain.builder()
                .queueCount(activeCount)
                .build();
    }

    public boolean checkQueue(QueueDomain queue) {
        long activeUsersCount = getActiveUserCount(queue).getQueueCount();

        return activeUsersCount >= MAX_ACTIVE_USERS;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public QueueDomain changeStatusToActive(QueueDomain queue) {
        QueueDomain.builder()
                .status("active")
                .validDate(LocalDateTime.now().plusMinutes(MAX_ACTIVE_MINUTES))
                .build();
        try {
            return queueRepository.save(queue);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("Status change to active error", e);

            return null;
        }
    }

    public QueueDomain getWaitingUserCountBeforeMe(QueueDomain queue) {
        List<QueueDomain> waitingQueues = queueRepository.findWaitingQueuesBeforeMe(queue.getToken());
        long waitingUserCountBeforeMe = waitingQueues.size();

        return QueueDomain.builder()
                .queueCount(waitingUserCountBeforeMe)
                .build();
    }

    public List<QueueDomain> getWaitingUserCountToActive(Long availableCount) {
        return queueRepository.findWaitingUserCountToActive(availableCount);
    }

    public List<QueueDomain> getActiveQueues() {
        return queueRepository.findActiveQueues();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteQueue(String token) {
        try {
            queueRepository.deleteById(token);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("Queue delete error", e);
        }
    }

    public Optional<QueueDomain> findQueueByUserId(Long userId) {
        return queueRepository.findByUserId(userId);
    }

    public List<QueueDomain> findAllQueueDomains() {
        return queueRepository.findAllQueues().stream()
                .map(queue -> QueueDomain.builder()
                        .token(queue.getToken())
                        .userId(queue.getUserId())
                        .status(queue.getStatus())
                        .validDate(queue.getValidDate())
                        .regiDate(queue.getRegiDate())
                        .build())
                .collect(Collectors.toList());
    }
}
