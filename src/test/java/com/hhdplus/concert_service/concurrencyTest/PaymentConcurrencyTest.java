package com.hhdplus.concert_service.concurrencyTest;

import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.business.service.UserService;
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
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentConcurrencyTest {

    static Logger LOGGER = LoggerFactory.getLogger(PaymentConcurrencyTest.class);

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("동시 결제 테스트")
    void executePaymentConcurrencyTest() throws InterruptedException, ExecutionException {
        //given
        long userId = 1L;
        long amount = 10000L;
        long useAmount = 100L;
        UserDomain user = UserDomain.builder().userId(userId).amount(amount).build();

        //when
        when(userRepository.findUserByIdWithPessimisticWrite(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserDomain.class))).thenReturn(user);

        int threadCount = 30; // 실행할 스레드 수
        CountDownLatch latch = new CountDownLatch(threadCount);

        Instant testStart = Instant.now();
        LOGGER.info("테스트 시작 시간 : {}", testStart);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        var futures = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        userService.useAmountUser(userId, useAmount);
                    } catch (Exception e) {
                        LOGGER.error("Thread {} encountered an error: {}", i, e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }, executorService))
                .toList();

        latch.await();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();

        Instant testEnd = Instant.now();
        LOGGER.info("테스트 종료 시간 : {}", testEnd);
        LOGGER.info("테스트 총 경과 시간 : {} ms", Duration.between(testStart, testEnd).toMillis());

        // then
        assertThat(user.getAmount()).isEqualTo(amount - (useAmount*threadCount));
    }
}