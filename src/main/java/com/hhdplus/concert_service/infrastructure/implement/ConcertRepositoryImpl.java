package com.hhdplus.concert_service.infrastructure.implement;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.repository.ConcertRepository;
import com.hhdplus.concert_service.infrastructure.entity.Concert;
import com.hhdplus.concert_service.infrastructure.entity.ConcertReservation;
import com.hhdplus.concert_service.infrastructure.repository.ConcertJpaRepository;
import com.hhdplus.concert_service.infrastructure.repository.ConcertReservationJpaRepository;
import com.hhdplus.concert_service.infrastructure.repository.ConcertScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertReservationJpaRepository concertReservationJpaRepository;

    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final ConcertJpaRepository concertJpaRepository;

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
        Optional<ConcertReservation> existingReservation = concertReservationJpaRepository.findById(concertSeat.getId());

        ConcertReservation reservation;
        if (existingReservation.isPresent()) {
            reservation = existingReservation.get();
            reservation.setStatus(concertSeat.getStatus());
            reservation.setConcertDate(concertSeat.getConcertDate());
            reservation.setSeatNo(concertSeat.getSeatNo());
            reservation.setUserId(concertSeat.getUserId());
            reservation.setConcertId(concertSeat.getConcertId());
        } else {
            reservation = ConcertReservation.builder()
                    .concertId(concertSeat.getConcertId())
                    .userId(concertSeat.getUserId())
                    .seatNo(concertSeat.getSeatNo())
                    .concertDate(concertSeat.getConcertDate())
                    .status(concertSeat.getStatus())
                    .build();
        }

        concertReservationJpaRepository.save(reservation);
    }

    @Override
    public ConcertDomain getUserReservation(Long concertId, LocalDateTime concertDate, Long seatNo) {
        return ConcertReservation.toDomain(concertReservationJpaRepository.findUserReservationByConcertIdAndDateAndSeatNo(concertId, concertDate, seatNo));
    }

    @Override
    public void save(ConcertDomain concertDomain) {
        Concert concert = Concert.builder()
                .id(concertDomain.getConcertId())
                .title(concertDomain.getTitle())
                .build();
        concertJpaRepository.save(concert);
    }

    @Override
    public Optional<ConcertDomain> findConcertReservation(Long id) {
        return concertReservationJpaRepository.findById(id).map(ConcertReservation::toDomain);
    }

}
