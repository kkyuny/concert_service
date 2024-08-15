package com.hhdplus.concert_service.loadTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.repository.ConcertRepository;
import com.hhdplus.concert_service.business.service.ConcertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@ExtendWith(MockitoExtension.class)
public class ConcertLoadTest {

    static Logger LOGGER = LoggerFactory.getLogger(ConcertLoadTest.class);

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private ConcertService concertService;

    @BeforeEach
    void setUp() {
        List<ConcertDomain> reservedSeats = new ArrayList<>();
        Random random = new Random();

        LOGGER.info("setUp 메서드 시작");

        // 100개의 콘서트, 각 콘서트마다 10개의 날짜, 각 날짜마다 10개의 좌석을 설정
        for (long concertId = 1L; concertId <= 1000L; concertId++) {
            long cid = random.nextInt(100) + 1;
            for (int day = 1; day <= 10; day++) {
                int rday = random.nextInt(10) + 1;
                LocalDateTime concertDate = LocalDateTime.of(2024, 8, rday, 19, 0);
                for (long seatNo = 1L; seatNo <= 10L; seatNo++) {
                    reservedSeats.add(ConcertDomain.builder()
                            .concertId(cid)
                            .concertDate(concertDate)
                            .seatNo(seatNo)
                            .build());
                }
            }
        }

        when(concertRepository.findReservedSeatsByConcertIdAndDate(5L, LocalDateTime.of(2024, 8, 1, 19, 0)))
                .thenReturn(reservedSeats);

        LOGGER.info("setUp 메서드 종료");
    }

    @Test
    @DisplayName("예약가능 좌석 테스트")
    void findReservedSeatsTest() throws ExecutionException, InterruptedException {
        Instant testStart = Instant.now();
        LOGGER.info("테스트 시작 시간 : {}", testStart);

        // Given: 콘서트 ID와 콘서트 날짜
        int numberOfUsers = 5000;
        Long concertId = 5L;
        LocalDateTime concertDate = LocalDateTime.of(2024, 8, 1, 19, 0);

        for (int i = 0; i < numberOfUsers; i++) {
            // When
            List<Long> result = concertService.findReservedSeats(concertId, concertDate);
            List<Long> expectedAvailableSeats = LongStream.rangeClosed(11, 50)
                    .boxed()
                    .collect(Collectors.toList());
            // Then
            assertThat(result).isNotNull();
            assertThat(result).containsExactlyInAnyOrderElementsOf(expectedAvailableSeats);
        }

        Instant testEnd = Instant.now();
        LOGGER.info("테스트 종료 시간 : {}", testEnd);
        LOGGER.info("테스트 총 경과 시간 : {} ms", Duration.between(testStart, testEnd).toMillis());
    }
}
