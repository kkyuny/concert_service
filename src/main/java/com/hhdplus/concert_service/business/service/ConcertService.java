package com.hhdplus.concert_service.business.service;

import com.hhdplus.concert_service.business.repository.ConcertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ConcertService {

    @Autowired
    ConcertRepository concertRepository;

    public List<LocalDate> findAvailableDates(Long concertId) {
        // 콘서트 ID로 예약 가능한 날짜를 조회
        return concertRepository.findAvailableDatesByConcertId(concertId)
                .stream()
                .map(ConcertDate::getDate) // Assuming ConcertDate has a method getDate()
                .collect(Collectors.toLkist());
    }

    public List<Integer> findReservedSeats(Long concertId, LocalDate concertDate) {
        // 예약된 좌석 번호를 조회
        List<Integer> reservedSeats = concertRepository.findReservedSeatsByConcertIdAndDate(concertId, concertDate)
                .stream()
                .map(ReservedSeat::getSeatNumber) // Assuming ReservedSeat has a method getSeatNumber()
                .collect(Collectors.toList());

        // 좌석 번호 1~50 중 예약되지 않은 좌석 번호 반환
        return IntStream.rangeClosed(1, 50)
                .filter(seat -> !reservedSeats.contains(seat))
                .boxed()
                .collect(Collectors.toList());
    }

    public boolean reserveSeat(Long concertId, LocalDate concertDate, int seatNumber) {
        // 좌석 예약
        if (concertRepository.existsByConcertIdAndDateAndSeatNumber(concertId, concertDate, seatNumber)) {
            return false; // 좌석이 이미 예약되어 있는 경우
        }

        ReservedSeat reservedSeat = new ReservedSeat(concertId, concertDate, seatNumber);
        concertRepository.saveReservedSeat(reservedSeat);
        return true; // 예약 성공
    }
}
