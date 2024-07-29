package com.hhdplus.concert_service.infrastructure.repository;

import com.hhdplus.concert_service.infrastructure.entity.Queue;
import com.hhdplus.concert_service.infrastructure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<Queue, String> {
    @Query("SELECT * FROM queue q WHERE q.no < :no AND q.status = 'active'")
    List<Queue> findActiveQueues(@Param("no") Long no);
}
