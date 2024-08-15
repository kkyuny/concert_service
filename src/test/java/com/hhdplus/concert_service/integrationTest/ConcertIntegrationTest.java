package com.hhdplus.concert_service.integrationTest;

import com.hhdplus.concert_service.application.dto.ConcertFacadeDto;
import com.hhdplus.concert_service.application.facade.ConcertFacade;
import com.hhdplus.concert_service.infrastructure.entity.Concert;
import com.hhdplus.concert_service.infrastructure.entity.ConcertReservation;
import com.hhdplus.concert_service.infrastructure.entity.ConcertSchedule;
import com.hhdplus.concert_service.infrastructure.repository.ConcertJpaRepository;
import com.hhdplus.concert_service.infrastructure.repository.ConcertReservationJpaRepository;
import com.hhdplus.concert_service.infrastructure.repository.ConcertScheduleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ConcertIntegrationTest {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private ConcertReservationJpaRepository concertReservationJpaRepository;

    private Concert testConcert;
    private List<ConcertSchedule> testConcertSchedules;

    @BeforeEach
    void setUp() {
        // 콘서트 엔티티 생성 및 저장
        testConcert = Concert.builder()
                .title("Test Concert")
                .regiDate(LocalDateTime.now())
                .build();
        testConcert = concertJpaRepository.save(testConcert);

        // 여러 개의 콘서트 일정 엔티티 생성 및 저장
        testConcertSchedules = Arrays.asList(
                ConcertSchedule.builder()
                        .concertId(testConcert.getId())
                        .price(100L)
                        .concertDate(LocalDateTime.now())
                        .regiDate(LocalDateTime.now())
                        .build(),
                ConcertSchedule.builder()
                        .concertId(testConcert.getId())
                        .price(150L)
                        .concertDate(LocalDateTime.now().plusDays(1))
                        .regiDate(LocalDateTime.now())
                        .build(),
                ConcertSchedule.builder()
                        .concertId(testConcert.getId())
                        .price(200L)
                        .concertDate(LocalDateTime.now().plusDays(2))
                        .regiDate(LocalDateTime.now())
                        .build()
        );
        testConcertSchedules = concertScheduleJpaRepository.saveAll(testConcertSchedules);
    }

    @Test
    @DisplayName("예약 가능한 날짜 조회 테스트")
    void getAvailableDatesTest() {
        ConcertFacadeDto dto = ConcertFacadeDto.builder().concertId(testConcert.getId()).build();
        ConcertFacadeDto result = concertFacade.getAvailableDates(dto);

        assertThat(result.getAvailableDates()).containsAll(
                Arrays.asList(
                        testConcertSchedules.get(0).getConcertDate(),
                        testConcertSchedules.get(1).getConcertDate(),
                        testConcertSchedules.get(2).getConcertDate()
                )
        );
    }

    @Test
    @DisplayName("예약 가능한 좌석 조회 테스트")
    void getAvailableSeatsTest() {
        // 좌석 예약 엔티티 생성 및 저장
        ConcertReservation testReservation = ConcertReservation.builder()
                .concertId(testConcert.getId())
                .concertDate(testConcertSchedules.get(0).getConcertDate())
                .seatNo(1L)
                .userId(1L)
                .status("reserved")
                .regiDate(LocalDateTime.now())
                .build();
        concertReservationJpaRepository.save(testReservation);

        ConcertFacadeDto dto = ConcertFacadeDto.builder()
                .concertId(testConcert.getId())
                .concertDate(testConcertSchedules.get(0).getConcertDate())
                .build();

        ConcertFacadeDto result = concertFacade.getAvailableSeats(dto);

        assertThat(result.getAvailableSeats()).doesNotContain(1L); // 예약된 좌석 번호
    }

    @Test
    @DisplayName("좌석 예약 테스트")
    void reserveSeatTest() {
        ConcertFacadeDto dto = ConcertFacadeDto.builder()
                .concertId(testConcert.getId())
                .concertDate(testConcertSchedules.get(0).getConcertDate())
                .status("reserved")
                .seatNo(2L)
                .userId(1L)
                .build();
        ConcertFacadeDto result = concertFacade.reserveSeat(dto);

        assertThat(result).isNotNull();
        assertThat(result.getSeatNo()).isEqualTo(2L);
        assertThat(result.getConcertDate()).isEqualTo(dto.getConcertDate());

        // 예약이 실제로 저장되었는지 확인
        boolean exists = concertReservationJpaRepository.existsByConcertIdAndConcertDateAndSeatNo(
                result.getConcertId(), result.getConcertDate(), 2L);
        assertThat(exists).isTrue();
    }
}