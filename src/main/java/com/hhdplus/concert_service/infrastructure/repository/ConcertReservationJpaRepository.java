package com.hhdplus.concert_service.infrastructure.repository;

import com.hhdplus.concert_service.infrastructure.entity.ConcertReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertReservationJpaRepository extends JpaRepository<ConcertReservation, Long> {
    @Query("SELECT r.seatNo FROM ConcertReservation r WHERE r.concertId = :concertId AND r.concertDate = :concertDate")
    List<Long> findReservedSeatsByConcertIdAndDate(@Param("concertId") Long concertId, @Param("concertDate") LocalDateTime concertDate);

    boolean existsByConcertIdAndConcertDateAndSeatNo(Long concertId, LocalDateTime concertDate, Long seatNo);

    @Query("SELECT r FROM ConcertReservation r WHERE r.concertId = :concertId AND r.concertDate = :concertDate AND r.seatNo = :seatNo")
    ConcertReservation findUserReservationByConcertIdAndDateAndSeatNo(@Param("concertId") Long concertId, @Param("concertDate") LocalDateTime concertDate, @Param("seatNo") Long seatNo);
}
