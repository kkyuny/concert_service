package com.hhdplus.concert_service.infrastructure.repository;

import com.hhdplus.concert_service.infrastructure.entity.Concert;
import com.hhdplus.concert_service.infrastructure.entity.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long> {
    @Query("SELECT s.concertDate FROM ConcertSchedule s WHERE s.concertId = :concertId")
    List<LocalDateTime> findAvailableDatesByConcertId(@Param("concertId") Long concertId);
}
