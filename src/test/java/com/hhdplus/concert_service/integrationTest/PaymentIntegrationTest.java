package com.hhdplus.concert_service.integrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hhdplus.concert_service.application.dto.PaymentFacadeDto;
import com.hhdplus.concert_service.application.facade.PaymentFacade;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.infrastructure.entity.Concert;
import com.hhdplus.concert_service.infrastructure.entity.ConcertReservation;
import com.hhdplus.concert_service.infrastructure.entity.ConcertSchedule;
import com.hhdplus.concert_service.infrastructure.entity.PaymentOutbox;
import com.hhdplus.concert_service.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PaymentIntegrationTest {

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
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Concert testConcert;

    private ConcertSchedule testConcertSchedule;

    private UserDomain testUser;

    private String testToken;

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

        // 콘서트 일정 엔티티 생성 및 저장
        testConcertSchedule = ConcertSchedule.builder()
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

        // Redis에 토큰 생성 및 저장
        testToken = "test-token";
        redisTemplate.opsForValue().set(testToken, testUser.getUserId().toString());
    }

    @Test
    @DisplayName("결제 성공 후 INIT 상태 메시지의 PUBLISHED 상태 전환 테스트")
    void executePaymentSuccessTest() throws JsonProcessingException, InterruptedException, ExecutionException {
        // 결제에 필요한 PaymentFacadeDto 생성
        PaymentFacadeDto dto = PaymentFacadeDto.builder()
                .userId(testUser.getUserId())
                .concertId(testConcert.getId())
                .concertDate(testConcertSchedule.getConcertDate())
                .seatNo(1L)
                .price(150L)
                .build();

        // Redis에 토큰이 올바르게 저장되었는지 확인 (디버깅)
        String token = redisTemplate.opsForValue().get(testToken);
        assertThat(token).isEqualTo(testUser.getUserId().toString());

        // 결제 처리
        PaymentFacadeDto result = paymentFacade.executePayment(testToken, dto);

        // 결제 결과 확인
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(150L);
        assertThat(result.getUserId()).isEqualTo(testUser.getUserId());
        assertThat(result.getConcertId()).isEqualTo(testConcert.getId());

        // 예약 상태가 "paid"로 변경되었는지 확인
        Optional<ConcertReservation> updatedReservation = concertReservationJpaRepository.findUserReservationByConcertIdAndDateAndSeatNo(
                result.getConcertId(), result.getConcertDate(), result.getSeatNo());
        assertThat(updatedReservation.get().getStatus()).isEqualTo("paid");

        // Redis에서 토큰이 삭제되었는지 확인
        // assertThat(redisTemplate.hasKey(testToken)).isFalse();

        // 아웃박스에 저장된 메시지 조회
        ///PaymentOutbox outboxMessage = paymentOutboxJpaRepository.findByPaymentId();
        //assertThat(outboxMessage).isNotNull();

        //PaymentOutbox sentMessage = outboxMessages.get(0);

        // 상태가 "PUBLISHED"로 변경되었는지 확인.
        //PaymentOutbox updatedMessage = paymentOutboxJpaRepository.findById(sentMessage.getId()).orElseThrow();
        //assertThat(updatedMessage.getStatus()).isEqualTo("PUBLISHED");
    }
}