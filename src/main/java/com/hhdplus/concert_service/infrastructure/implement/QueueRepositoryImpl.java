package com.hhdplus.concert_service.infrastructure.implement;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.repository.QueueRepository;
import com.hhdplus.concert_service.infrastructure.entity.Queue;
import com.hhdplus.concert_service.infrastructure.repository.QueueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {
    private final QueueJpaRepository jpaRepository;

    @Override
    public QueueDomain save(QueueDomain queue) {
        return Queue.toDomain(jpaRepository.save(Queue.toEntity(queue)));
    }

    @Override
    public Optional<QueueDomain> findById(String token) {
        return jpaRepository.findById(token).map(Queue::toDomain);
    }

    @Override
    public List<QueueDomain> findActiveQueues(String token) {
        return jpaRepository.findActiveQueues(token)
                .stream()
                .map(Queue::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QueueDomain> findActiveQueues() {
        return jpaRepository.findActiveQueues()
                .stream()
                .map(Queue::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QueueDomain> findWaitingQueuesBeforeMe(String token) {
        return jpaRepository.findWaitingQueuesBeforeMe(token).stream()
                .map(Queue::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QueueDomain> findWaitingUserCountToActive(Long availableCount) {
        PageRequest pageable = PageRequest.of(0, availableCount.intValue());

        return jpaRepository.findWaitingUserCountToActive(pageable).stream()
                .map(Queue::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String token) {
        jpaRepository.deleteById(token);
    }

    @Override
    public Optional<QueueDomain> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).map(Queue::toDomain);
    }

}
