package com.hhdplus.concert_service.integrationTest;

import com.hhdplus.concert_service.application.dto.ChargeFacadeDto;
import com.hhdplus.concert_service.application.facade.ChargeFacade;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ChargeConcurrencyTest {

    @Autowired
    private ChargeFacade chargeFacade;

    @Autowired
    private UserRepository userRepository;

    private UserDomain testUser;

    @BeforeEach
    void setUp() {
        testUser = UserDomain.builder().userId(1L).amount(1000L).build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("유저 금액 동시 충전 테스트")
    void concurrentChargeUserAmountTest() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    ChargeFacadeDto dto = ChargeFacadeDto.builder().userId(testUser.getUserId()).amount(100L).build();
                    chargeFacade.chargeUserAmount(dto);
                } finally {
                    latch.countDown();
                }
            });
        }

        UserDomain updatedUser = userRepository.findById(testUser.getUserId()).orElseThrow();
        assertThat(updatedUser.getAmount()).isEqualTo(1000L + 100L); // 낙관락으로 1개만 수행
    }
}