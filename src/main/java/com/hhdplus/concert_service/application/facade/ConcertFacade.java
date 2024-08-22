package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.ConcertFacadeDto;
import com.hhdplus.concert_service.business.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Component
@RequiredArgsConstructor
public class ConcertFacade {
    private static final int MAX_SEAT_NO = 50;

    @Autowired
    ConcertService concertService;

    // 예약 가능한 날짜 조회
    public ConcertFacadeDto getAvailableDates(ConcertFacadeDto dto) {
        // 콘서트 ID로 예약 가능한 날짜 조회
        Long concertId = dto.getConcertId();
        List<LocalDateTime> availableDates = concertService.findAvailableDates(concertId);

        return ConcertFacadeDto.builder()
                .concertId(concertId)
                .availableDates(availableDates)
                .build();
    }

    // 예약 가능한 좌석 조회
    public ConcertFacadeDto getAvailableSeats(Long concertId, LocalDateTime concertDate) {
        // 예약안된 좌석 번호 목록
        List<Long> availableSeats = concertService.findReservedSeats(concertId, concertDate);

        return ConcertFacadeDto.builder()
                .concertId(concertId)
                .concertDate(concertDate)
                .availableSeats(availableSeats)
                .build();
    }

    // 좌석 예약
    public ConcertFacadeDto reserveSeat(ConcertFacadeDto facadeDto) {
        Long concertId = facadeDto.getConcertId();
        LocalDateTime concertDate = facadeDto.getConcertDate();
        Long seatNo = facadeDto.getSeatNo();
        Long userId = facadeDto.getUserId();

        // 좌석 예약 요청
        concertService.reserveSeat(concertId, concertDate, userId, seatNo);

        return ConcertFacadeDto.builder()
                .concertId(concertId)
                .concertDate(concertDate)
                .seatNo(seatNo)
                .build();
    }

    public void cancelReservation() {
    }
}
