package com.hhdplus.concert_service.infrastructure.implement;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.repository.ConcertRepository;
import com.hhdplus.concert_service.infrastructure.entity.Concert;
import com.hhdplus.concert_service.infrastructure.entity.ConcertReservation;
import com.hhdplus.concert_service.infrastructure.entity.ConcertSchedule;
import com.hhdplus.concert_service.infrastructure.repository.ConcertJpaRepository;
import com.hhdplus.concert_service.infrastructure.repository.ConcertReservationJpaRepository;
import com.hhdplus.concert_service.infrastructure.repository.ConcertScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    @Autowired
    ConcertReservationJpaRepository concertReservationJpaRepository;

    @Autowired
    ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Override
    public List<ConcertDomain> findAvailableDatesByConcertId(Long concertId) {
        List<LocalDateTime> availableDates = concertScheduleJpaRepository.findAvailableDatesByConcertId(concertId);

        return availableDates.stream()
                .map(date -> ConcertDomain.builder().concertId(concertId).concertDate(date).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ConcertDomain> findReservedSeatsByConcertIdAndDate(Long concertId, LocalDateTime concertDate) {
        List<Long> reservedSeats = concertReservationJpaRepository.findReservedSeatsByConcertIdAndDate(concertId, concertDate);
        return reservedSeats.stream()
                .map(seatNo -> ConcertDomain.builder().concertId(concertId).concertDate(concertDate).seatNo(seatNo).build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByConcertIdAndDateAndSeatNo(Long concertId, LocalDateTime concertDate, Long seatNo) {
        return concertReservationJpaRepository.existsByConcertIdAndConcertDateAndSeatNo(concertId, concertDate, seatNo);
    }

    @Override
    public void saveConcertReservation(ConcertDomain concertSeat) {
        ConcertReservation reservation = new ConcertReservation();

        reservation.setConcertId(concertSeat.getConcertId());
        reservation.setUserId(concertSeat.getUserId());
        reservation.setSeatNo(concertSeat.getSeatNo());
        concertReservationJpaRepository.save(reservation);
    }

    @Override
    public ConcertDomain getUserReservation(Long concertId, LocalDateTime concertDate, Long seatNo) {
        return concertReservationJpaRepository.findUserReservationByConcertIdAndDateAndSeatNo(concertId, concertDate, seatNo);
    }
}
