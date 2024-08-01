package com.hhdplus.concert_service.integrationTest;

import com.hhdplus.concert_service.application.dto.PaymentFacadeDto;
import com.hhdplus.concert_service.application.facade.PaymentFacade;
import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.business.service.*;
import com.hhdplus.concert_service.infrastructure.entity.Concert;
import com.hhdplus.concert_service.infrastructure.entity.ConcertReservation;
import com.hhdplus.concert_service.infrastructure.entity.ConcertSchedule;
import com.hhdplus.concert_service.infrastructure.entity.Queue;
import com.hhdplus.concert_service.infrastructure.repository.*;
import com.hhdplus.concert_service.interfaces.common.exception.BadRequestException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PaymentFacadeIntegrationTest {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    private ConcertReservationJpaRepository concertReservationJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueueJpaRepository queueJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private Concert testConcert;

    private ConcertSchedule testConcertSchedule;

    private UserDomain testUser;

    private Queue testQueue;

    @BeforeEach
    void setUp() {
        // 유저 엔티티 생성 및 저장
        testUser = UserDomain.builder().userId(1L).amount(1000L).build();
        testUser = userRepository.save(testUser);

        // 콘서트 엔티티 생성 및 저장
        testConcert = Concert.builder()
                .title("Test Concert")
                .regiDate(LocalDateTime.now())
                .build();
        testConcert = concertJpaRepository.save(testConcert);

        // 하나의 콘서트 일정 엔티티 생성 및 저장
        testConcertSchedule = ConcertSchedule.builder()
                .id(1L)
                .concertId(testConcert.getId())
                .price(150L)
                .concertDate(LocalDateTime.now())
                .regiDate(LocalDateTime.now())
                .build();
        testConcertSchedule = concertScheduleJpaRepository.save(testConcertSchedule);

        // 콘서트 예약 엔티티 생성 및 저장
        ConcertReservation reservation = ConcertReservation.builder()
                .concertId(testConcert.getId())
                .concertDate(testConcertSchedule.getConcertDate())
                .seatNo(1L)
                .userId(testUser.getUserId())
                .status("waiting")
                .regiDate(LocalDateTime.now())
                .build();
        concertReservationJpaRepository.save(reservation);

        // 큐 엔티티 생성 및 저장
        testQueue = queueJpaRepository.save(Queue.builder()
                .token("test-token")
                .userId(testUser.getUserId())
                .status("active")
                .validDate(LocalDateTime.now().plusMinutes(5))
                .regiDate(LocalDateTime.now())
                .build());
    }

    @Test
    @DisplayName("결제 성공 테스트")
    void executePaymentSuccessTest() {
        PaymentFacadeDto dto = PaymentFacadeDto.builder()
                .id(1L)
                .userId(testUser.getUserId())
                .concertId(testConcert.getId())
                .concertDate(testConcertSchedule.getConcertDate())
                .seatNo(1L)
                .price(150L)
                .build();

        PaymentFacadeDto result = paymentFacade.executePayment(dto);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(150L);
        assertThat(result.getUserId()).isEqualTo(testUser.getUserId());
        assertThat(result.getConcertId()).isEqualTo(testConcert.getId());

        // 결제 후 예약 상태가 "paid"로 변경되었는지 확인
        ConcertReservation updatedReservation = concertReservationJpaRepository.findUserReservationByConcertIdAndDateAndSeatNo(
                testConcert.getId(), testConcertSchedule.getConcertDate(), 1L);
        assertThat(updatedReservation.getStatus()).isEqualTo("paid");

        // 큐가 삭제되었는지 확인
        assertThat(queueJpaRepository.findById("test-token")).isEmpty();
    }
}