package com.hhdplus.concert_service.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.repository.ConcertRepository;
import com.hhdplus.concert_service.business.service.ConcertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private ConcertService concertService;

    @Test
    @DisplayName("콘서트 일정 조회 테스트")
    public void findAvailableDatesTest() {
        //given
        List<ConcertDomain> availableDates = Arrays.asList(
                ConcertDomain.builder().concertId(1L).concertDate(LocalDateTime.of(2024, 8, 24, 19, 0)).build(),
                ConcertDomain.builder().concertId(1L).concertDate(LocalDateTime.of(2024, 8, 31, 19, 0)).build()
        );

        ConcertDomain concert = ConcertDomain.builder().concertId(1L).build();

        when(concertRepository.findAvailableDatesByConcertId(concert.getConcertId())).thenReturn(availableDates);

        //when
        List<LocalDateTime> result = concertService.findAvailableDates(concert.getConcertId());

        //then
        assertEquals(2, result.size());
        assertTrue(result.contains(LocalDateTime.of(2024, 8, 24, 19, 0)));
        assertTrue(result.contains(LocalDateTime.of(2024, 8, 31, 19, 0)));
    }

    @Test
    @DisplayName("콘서트 좌석 조회 테스트")
    public void findReservedSeatsTest() {
        //given
        LocalDateTime concertDate = LocalDateTime.of(2024, 8, 24, 19, 0);

        List<ConcertDomain> reservedSeats = Arrays.asList(
            ConcertDomain.builder().concertId(1L).concertDate(LocalDateTime.of(2024, 8, 24, 19, 0)).seatNo(1L).build(),
            ConcertDomain.builder().concertId(1L).concertDate(LocalDateTime.of(2024, 8, 24, 19, 0)).seatNo(2L).build(),
            ConcertDomain.builder().concertId(1L).concertDate(LocalDateTime.of(2024, 8, 24, 19, 0)).seatNo(3L).build()
        );

        when(concertRepository.findReservedSeatsByConcertIdAndDate(1L, concertDate)).thenReturn(reservedSeats);

        //when
        List<Long> result = concertService.findReservedSeats(1L, concertDate);

        //then
        assertEquals(47, result.size());
        assertFalse(result.contains(1L));
        assertFalse(result.contains(2L));
        assertFalse(result.contains(3L));
        assertTrue(result.contains(4L));
    }

    @Test
    @DisplayName("콘서트 좌석 예약 테스트")
    public void reserveSeatTest() {
        Long concertId = 1L;
        LocalDateTime concertDate = LocalDateTime.of(2024, 8, 31, 19, 0);
        Long seatNo = 1L;
        Long userId = 1L;

        when(concertRepository.existsByConcertIdAndDateAndSeatNo(concertId, concertDate, seatNo)).thenReturn(false);

        boolean result = concertService.reserveSeat(concertId, concertDate, seatNo, userId);

        assertTrue(result);
        verify(concertRepository, times(1)).saveConcertReservation(any(ConcertDomain.class));
    }

    @Test
    public void reserveSeatFailTest() {
        Long concertId = 1L;
        LocalDateTime concertDate = LocalDateTime.of(2024, 8, 31, 19, 0);
        Long seatNo = 1L;
        Long userId = 1L;

        when(concertRepository.existsByConcertIdAndDateAndSeatNo(concertId, concertDate, seatNo)).thenReturn(true);

        boolean result = concertService.reserveSeat(concertId, concertDate, seatNo, userId);

        assertFalse(result);
        verify(concertRepository, never()).saveConcertReservation(any(ConcertDomain.class));
    }
}
