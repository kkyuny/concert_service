package com.hhdplus.concert_service.infrastructure.implement;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.repository.QueueRepository;
import com.hhdplus.concert_service.infrastructure.entity.Queue;
import com.hhdplus.concert_service.infrastructure.entity.User;
import com.hhdplus.concert_service.infrastructure.repository.QueueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    @Autowired
    QueueJpaRepository jpaRepository;

    @Override
    public QueueDomain save(QueueDomain queue) {
        return Queue.toDomain(jpaRepository.save(Queue.toEntity(queue)));
    }

    @Override
    public Optional<QueueDomain> findById(String token) {
        return jpaRepository.findById(token).map(Queue::toDomain);
    }

    @Override
    public List<QueueDomain> findActiveQueues(Long no) {
        return jpaRepository.findActiveQueues(no)
                .stream()
                .map(Queue::toDomain)
                .toList();
    }
}
