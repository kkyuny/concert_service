package com.hhdplus.concert_service.business.repository;

public interface QueueRedisRepository {

    void addQueue(String token);
    Long getQueueOrder(String token);
    Boolean isTokenActive(String token);
    void activateTokens();
    void expireToken(String token);
    void expireTokens();
}
