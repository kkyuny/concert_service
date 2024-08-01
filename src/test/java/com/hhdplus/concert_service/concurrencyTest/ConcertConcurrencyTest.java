package com.hhdplus.concert_service.integrationTest;

import com.hhdplus.concert_service.application.dto.ConcertFacadeDto;
import com.hhdplus.concert_service.application.facade.ConcertFacade;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.infrastructure.entity.*;
import com.hhdplus.concert_service.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ConcertConcurrencyTest {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private ConcertReservationJpaRepository concertReservationJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private Concert testConcert;
    private ConcertSchedule testConcertSchedule;
    private UserDomain testUser;

    private final LocalDateTime fixedConcertDate = LocalDateTime.of(2024, 8, 3, 20, 0);

    @BeforeEach
    void setUp() {
        // 이전 데이터 제거
        concertReservationJpaRepository.deleteAll();
        concertScheduleJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        // 콘서트 엔티티 생성 및 저장
        testConcert = Concert.builder()
                .title("Test Concert")
                .regiDate(LocalDateTime.now())
                .build();
        testConcert = concertJpaRepository.save(testConcert);

        // 하나의 콘서트 일정 엔티티 생성 및 저장
        testConcertSchedule = ConcertSchedule.builder()
                .concertId(testConcert.getId())
                .price(100L)
                .concertDate(fixedConcertDate)
                .regiDate(LocalDateTime.now())
                .build();
        testConcertSchedule = concertScheduleJpaRepository.save(testConcertSchedule);
    }

    @Test
    @DisplayName("좌석 예약 동시성 테스트")
    void concurrentReserveSeatTest() throws InterruptedException {
        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int seatNo = i % 5 + 1;  // 1부터 5까지 좌석 번호 순환
            executorService.execute(() -> {
                try {
                    ConcertFacadeDto dto = ConcertFacadeDto.builder()
                            .concertId(testConcert.getId())
                            .concertDate(testConcertSchedule.getConcertDate())
                            .status("reserved")
                            .seatNo((long) seatNo)
                            .userId(1L)
                            .build();
                    concertFacade.reserveSeat(dto);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        // 예약된 좌석 번호 확인
        for (int seatNo = 1; seatNo <= 5; seatNo++) {
            boolean exists = concertReservationJpaRepository.existsByConcertIdAndConcertDateAndSeatNo(
                    testConcert.getId(), testConcertSchedule.getConcertDate(), (long) seatNo);
            assertThat(exists).isTrue();
        }
    }
}