package com.hhdplus.concert_service.concurrencyTest;

import com.hhdplus.concert_service.application.dto.PaymentFacadeDto;
import com.hhdplus.concert_service.application.facade.PaymentFacade;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.infrastructure.entity.Concert;
import com.hhdplus.concert_service.infrastructure.entity.ConcertReservation;
import com.hhdplus.concert_service.infrastructure.entity.ConcertSchedule;
import com.hhdplus.concert_service.infrastructure.entity.Queue;
import com.hhdplus.concert_service.infrastructure.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PaymentFacadeConcurrencyTest {

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

    @PersistenceContext
    private EntityManager entityManager;

    private Concert testConcert;

    private ConcertSchedule testConcertSchedule;

    private UserDomain testUser;

    @BeforeEach
    void setUp() {
        // 모든 데이터 삭제
        queueJpaRepository.deleteAll();
        concertReservationJpaRepository.deleteAll();
        concertScheduleJpaRepository.deleteAll();
        concertJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        // 유저 엔티티 생성 및 저장
        testUser = UserDomain.builder().userId(0L).amount(1000L).build();
        testUser = userRepository.save(testUser);
        System.out.println("Saved User: " + testUser);

        // 콘서트 엔티티 생성 및 저장
        testConcert = Concert.builder()
                .title("Test Concert")
                .regiDate(LocalDateTime.now())
                .build();
        testConcert = concertJpaRepository.save(testConcert);
        System.out.println("Saved Concert: " + testConcert);

        // 콘서트 일정 엔티티 생성 및 저장
        testConcertSchedule = ConcertSchedule.builder()
                .concertId(testConcert.getId())
                .price(150L)
                .concertDate(LocalDateTime.now())
                .regiDate(LocalDateTime.now())
                .build();
        testConcertSchedule = concertScheduleJpaRepository.save(testConcertSchedule);
        System.out.println("Saved ConcertSchedule: " + testConcertSchedule);

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
        System.out.println("Saved Reservation: " + reservation);

        // 토큰 생성 및 저장
        Queue testQueue = Queue.builder()
                .token("test-token")
                .userId(testUser.getUserId())
                .status("active")
                .validDate(LocalDateTime.now().plusMinutes(5))
                .regiDate(LocalDateTime.now())
                .build();
        queueJpaRepository.save(testQueue);
        System.out.println("Saved Queue: " + testQueue);

        // 플러시 및 클리어
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("동시 결제 성공 테스트")
    void executePaymentConcurrencyTest() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        PaymentFacadeDto dto = PaymentFacadeDto.builder()
                .userId(testUser.getUserId())
                .concertId(testConcert.getId())
                .concertDate(testConcertSchedule.getConcertDate())
                .seatNo(1L)
                .price(150L)
                .build();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentFacade.executePayment(dto);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 결제 후 예약 상태가 "paid"로 변경되었는지 확인
        ConcertReservation updatedReservation = concertReservationJpaRepository.findUserReservationByConcertIdAndDateAndSeatNo(
                testConcert.getId(), testConcertSchedule.getConcertDate(), 1L);
        assertThat(updatedReservation).isNotNull();
        assertThat(updatedReservation.getStatus()).isEqualTo("paid");

        // 큐가 삭제되었는지 확인
        assertThat(queueJpaRepository.findById("test-token")).isEmpty();
    }
}