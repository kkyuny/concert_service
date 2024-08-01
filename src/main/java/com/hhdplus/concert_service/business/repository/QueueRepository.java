package com.hhdplus.concert_service.business.repository;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {
    QueueDomain save(QueueDomain queue);

    Optional<QueueDomain> findById(String token);

    List<QueueDomain> findActiveQueues(String token);

    List<QueueDomain> findActiveQueues();

    List<QueueDomain> findWaitingQueuesBeforeMe(String token);

    List<QueueDomain> findWaitingUserCountToActive(Long availableCount);

    void deleteById(String token);

    QueueDomain findTokenByUserId(Long userId);
}
