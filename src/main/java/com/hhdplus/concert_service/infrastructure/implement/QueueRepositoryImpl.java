package com.hhdplus.concert_service.infrastructure.implement;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.repository.QueueRepository;
import com.hhdplus.concert_service.infrastructure.redis.QueueRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {
    private final QueueRedisRepository redisRepository;

    @Override
    public void save(QueueDomain queue) {
        String token = queue.getToken();
        redisRepository.addQueue(token);
    }

    @Override
    public Long getQueueOrder(String token) {
        return redisRepository.getQueueOrder(token);
    }

    @Override
    public Boolean verifyToken(String token) {
        return redisRepository.isTokenActive(token);
    }

    @Override
    public void expireToken(String token) {
        redisRepository.expireToken(token);
    }

    @Override
    public void activateTokens() {
        redisRepository.activateTokens();
    }

    @Override
    public void expireTokens() {
        redisRepository.expireTokens();
    }

}
