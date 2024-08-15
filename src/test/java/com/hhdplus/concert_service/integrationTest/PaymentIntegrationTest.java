package com.hhdplus.concert_service.integrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hhdplus.concert_service.application.dto.PaymentFacadeDto;
import com.hhdplus.concert_service.application.facade.PaymentFacade;
import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import com.hhdplus.concert_service.business.message.PaymentMessageSender;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.business.service.*;
import com.hhdplus.concert_service.infrastructure.entity.*;
import com.hhdplus.concert_service.infrastructure.repository.*;
import com.hhdplus.concert_service.interfaces.common.exception.BadRequestException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    private QueueJpaRepository queueJpaRepository;

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Autowired
    PaymentMessageSender paymentMessageSender;

    @Autowired
    PaymentMessageOutboxWriter paymentMessageOutboxWriter;

    @PersistenceContext
    private EntityManager entityManager;

    private Concert testConcert;

    private ConcertSchedule testConcertSchedule;

    private UserDomain testUser;

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

        // 토큰 생성 및 저장
        Queue testQueue = Queue.builder()
                .token("test-token")
                .userId(testUser.getUserId())
                .status("active")
                .validDate(LocalDateTime.now().plusMinutes(5))
                .regiDate(LocalDateTime.now())
                .build();

        queueJpaRepository.save(testQueue);
    }

    @Test
    @DisplayName("결제 성공 테스트(outbox status -> init)")
    void executePaymentSuccessTest() {
        // 결제에 필요한 PaymentFacadeDto 생성
        PaymentFacadeDto dto = PaymentFacadeDto.builder()
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
        Optional<ConcertReservation> updatedReservation = concertReservationJpaRepository.findUserReservationByConcertIdAndDateAndSeatNo(
                result.getConcertId(), result.getConcertDate(), result.getSeatNo());
        assertThat(updatedReservation.get().getStatus()).isEqualTo("paid");

        // 큐가 삭제되었는지 확인
        assertThat(queueJpaRepository.findById("test-token")).isEmpty();

        List<PaymentOutbox> outboxMessages = paymentOutboxJpaRepository.findAll();
        assertThat(outboxMessages).isNotEmpty();

        // 아웃박스에 메시지가 "INIT" 상태로 저장되었는지 확인
        PaymentOutbox savedMessage = outboxMessages.get(0);
        assertThat(savedMessage.getStatus()).isEqualTo("INIT");
    }

    @Test
    @DisplayName("INIT 상태 메시지의 PUBLISHED 상태 전환 테스트")
    void messagePublishedStatusTest() throws JsonProcessingException, InterruptedException, ExecutionException {
        PaymentMessage message = PaymentMessage.builder()
                .id(1L)
                .userId(testUser.getUserId())
                .price(150L)
                .status("INIT")
                .build();
        paymentMessageOutboxWriter.save(message);

        // 아웃박스에 저장된 메시지 조회.
        List<PaymentOutbox> outboxMessages = paymentOutboxJpaRepository.findAll();
        assertThat(outboxMessages).isNotEmpty();

        PaymentOutbox sentMessage = outboxMessages.get(0);
        assertThat(sentMessage.getStatus()).isEqualTo("INIT");

        // 메시지를 발행하는 로직을 직접 실행하여 상태를 변경.
        paymentMessageSender.send(message);

        // 상태가 "PUBLISHED"로 변경되었는지 확인.
        PaymentOutbox updatedMessage = paymentOutboxJpaRepository.findById(message.getId()).orElseThrow();
        assertThat(updatedMessage.getStatus()).isEqualTo("PUBLISHED");
    }
}