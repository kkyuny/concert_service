package com.hhdplus.concert_service.business.repository;

import com.hhdplus.concert_service.business.domain.QueueDomain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {
    void save(QueueDomain queue);

    Long getQueueOrder(String token);

    Boolean verifyToken(String token);

    void expireToken(String token);

    void activateTokens();

    void expireTokens();

}
