package com.hhdplus.concert_service.business.service;

import com.hhdplus.concert_service.infrastructure.redis.QueueRedisRepository;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueRedisService {

    private final QueueRedisRepository queueRedisRepository;

    public void addQueue(String token) {
        queueRedisRepository.addQueue(token);
    }

    public Long getQueueOrder(String token) {
        Long rank = queueRedisRepository.getQueueOrder(token);
        if (rank == null) {
            throw new InvalidReqBodyException("INVALID_TOKEN");
        }
        return rank;
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
}
