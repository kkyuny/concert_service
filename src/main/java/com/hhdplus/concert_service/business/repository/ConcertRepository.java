package com.hhdplus.concert_service.business.repository;

import com.hhdplus.concert_service.business.domain.ConcertDomain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface ConcertRepository {
    List<ConcertDomain> findAvailableDatesByConcertId(Long concertId);

    List<ConcertDomain> findReservedSeatsByConcertIdAndDate(Long concertId, LocalDateTime concertDate);

    boolean existsByConcertIdAndDateAndSeatNo(Long concertId, LocalDateTime concertDate, Long seatNo);

    void saveConcertReservation(ConcertDomain concertSeat);

    ConcertDomain getUserReservation(Long concertId, LocalDateTime concertDate, Long seatNo);
}
