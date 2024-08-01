package com.hhdplus.concert_service.infrastructure.repository;

import com.hhdplus.concert_service.infrastructure.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
