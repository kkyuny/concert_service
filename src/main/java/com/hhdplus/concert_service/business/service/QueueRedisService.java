package com.hhdplus.concert_service.business.service;

import com.hhdplus.concert_service.business.repository.QueueRedisRepository;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueRedisService {
    static Logger LOGGER = LoggerFactory.getLogger(QueueRedisService.class);

    private final QueueRedisRepository queueRedisRepository;

    public void addQueue(String token) {
        queueRedisRepository.addQueue(token);
        LOGGER.info("token create success: " + token);
    }

    public Long getQueueOrder(String token) {
        Long rank = queueRedisRepository.getQueueOrder(token);
        if (rank == null) {
            throw new InvalidReqBodyException("INVALID_TOKEN");
        }
        LOGGER.info("queue order: " + rank);
        return rank;
    }

    public Boolean validateAndActivateToken(String token) {
        return queueRedisRepository.validateAndActivateToken(token);
    }

    public Boolean isTokenActive(String token) {
        return queueRedisRepository.isTokenActive(token);
    }

    public void activateTokens() {
        queueRedisRepository.activateTokens();
    }

    public void expireToken(String token) {
        if (!queueRedisRepository.isTokenActive(token)) {
            throw new InvalidReqBodyException("INVALID_TOKEN");
        }
        queueRedisRepository.expireToken(token);
    }

    public void expireTokens() {
        queueRedisRepository.expireTokens();
    }


    public Boolean verifyToken(String token) {
        return queueRedisRepository.isTokenInQueueOrActive(token);
    }
}
