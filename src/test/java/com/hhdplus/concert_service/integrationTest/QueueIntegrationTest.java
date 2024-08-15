package com.hhdplus.concert_service.integrationTest;

import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import com.hhdplus.concert_service.application.facade.QueueFacade;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.QueueRepository;
import com.hhdplus.concert_service.business.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class QueueIntegrationTest {

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueueRepository queueRepository;

    private UserDomain testUser;

    @BeforeEach
    void setUp() {
        testUser = UserDomain.builder().userId(1L).build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("토큰 생성 테스트")
    void createTokenTest() {
        QueueFacadeDto dto = QueueFacadeDto.builder().userId(testUser.getUserId()).build();
        QueueFacadeDto result = queueFacade.createToken(dto);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isNotEmpty();
    }

    @Test
    @DisplayName("대기열 상태 확인 테스트")
    void checkQueueTest() {
        QueueDomain queue = QueueDomain.builder()
                .token("testToken")
                .userId(testUser.getUserId())
                .status("waiting")
                .validDate(LocalDateTime.now().plusMinutes(5))
                .build();
        queue = queueRepository.save(queue);

        QueueFacadeDto dto = QueueFacadeDto.builder().token(queue.getToken()).build();
        QueueFacadeDto result = queueFacade.checkQueue(dto);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("waiting");
    }

    @Test
    @DisplayName("대기열 순서 조회 테스트")
    void getQueueOrderTest() {
        QueueDomain queue = QueueDomain.builder()
                .token("testToken")
                .userId(testUser.getUserId())
                .status("waiting")
                .validDate(LocalDateTime.now().plusMinutes(5))
                .build();
        queue = queueRepository.save(queue);

        QueueFacadeDto dto = QueueFacadeDto.builder().token(queue.getToken()).build();
        QueueFacadeDto result = queueFacade.getQueueOrder(dto);

        assertThat(result).isNotNull();
        assertThat(result.getQueueCount()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    @DisplayName("토큰 활성화 테스트")
    void activateTokensTest() {
        QueueDomain queue = QueueDomain.builder()
                .token("testToken")
                .userId(testUser.getUserId())
                .status("waiting")
                .validDate(LocalDateTime.now().plusMinutes(5))
                .build();
        queue = queueRepository.save(queue);

        queueFacade.activateTokens();

        List<QueueDomain> activeQueues = queueRepository.findActiveQueues();
        assertThat(activeQueues).isNotEmpty();
        assertThat(activeQueues.get(0).getStatus()).isEqualTo("active");
    }

    @Test
    @DisplayName("토큰 만료 테스트")
    void expireTokensTest() {
        QueueDomain queue = QueueDomain.builder()
                .token("testToken")
                .userId(testUser.getUserId())
                .status("active")
                .validDate(LocalDateTime.now().minusMinutes(1))
                .build();
        queue = queueRepository.save(queue);

        queueFacade.expireTokens();

        QueueDomain expiredQueue = queueRepository.findById(queue.getToken()).orElse(null);
        assertThat(expiredQueue).isNull();
    }
}