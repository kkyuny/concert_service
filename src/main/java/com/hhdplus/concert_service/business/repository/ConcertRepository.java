package com.hhdplus.concert_service.business.repository;

import com.hhdplus.concert_service.business.domain.ConcertDomain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository {
    List<ConcertDomain> findAvailableDatesByConcertId(Long concertId);

    List<ConcertDomain> findReservedSeatsByConcertIdAndDate(Long concertId, LocalDateTime concertDate);

    boolean existsByConcertIdAndDateAndSeatNo(Long concertId, LocalDateTime concertDate, Long seatNo);

    void saveConcertReservation(ConcertDomain reservedSeat);

    ConcertDomain getUserReservation(Long concertId, LocalDateTime concertDate, Long seatNo);

    void save(ConcertDomain concertDomain);

    Optional<ConcertDomain> findConcertReservation(Long id);
}
