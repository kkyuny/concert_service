package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.ConcertFacadeDto;
import com.hhdplus.concert_service.business.service.ConcertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Component
public class ConcertFacade {
    private static final int MAX_SEAT_NO = 50;

    @Autowired
    ConcertService concertService;

    // 예약 가능한 날짜 조회
    public ConcertFacadeDto getAvailableDates(ConcertFacadeDto dto) {
        // 콘서트 ID로 예약 가능한 날짜 조회
        Long concertId = dto.getConcertId();
        List<LocalDate> availableDates = concertService.findAvailableDates(concertId);

        return ConcertFacadeDto.builder()
                .concertId(concertId)
                .availableDates(availableDates)
                .build();
    }

    // 예약 가능한 좌석 조회
    public ConcertFacadeDto getAvailableSeats(ConcertFacadeDto dto) {
        Long concertId = dto.getConcertId();
        LocalDate concertDate = dto.getConcertDate();

        // 예약된 좌석 번호 목록을 가져옵니다.
        List<Long> reservedSeats = concertService.findReservedSeats(concertId, concertDate);

        // 예약되지 않은 좌석 번호를 필터링하여 목록을 생성합니다.
        List<Long> availableSeats = LongStream.rangeClosed(1, MAX_SEAT_NO)
                .filter(seat -> !reservedSeats.contains(seat))
                .boxed()
                .collect(Collectors.toList());

        return ConcertFacadeDto.builder()
                .concertId(concertId)
                .concertDate(concertDate)
                .availableSeats(availableSeats)
                .build();
    }

    // 좌석 예약
    public ConcertFacadeDto reserveSeat(ConcertFacadeDto facadeDto) {
        // 콘서트 ID, 날짜, 좌석 정보를 가져옵니다.
        Long concertId = facadeDto.getConcertId();
        LocalDate concertDate = facadeDto.getConcertDate();
        Long seatNo = facadeDto.getSeatNo();

        // 좌석 예약 요청
        boolean success = concertService.reserveSeat(concertId, concertDate, seatNo);

        // 결과를 DTO로 반환
        return ConcertFacadeDto.builder()
                .concertId(concertId)
                .concertDate(concertDate)
                .seatNo(seatNo)
                .build();
    }
}
