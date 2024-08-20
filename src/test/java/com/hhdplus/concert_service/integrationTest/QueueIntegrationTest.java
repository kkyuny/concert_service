package com.hhdplus.concert_service.integrationTest;

import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import com.hhdplus.concert_service.application.facade.QueueFacade;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.service.QueueService;
import com.hhdplus.concert_service.business.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QueueIntegrationTest {

    private static final String QUEUE = "queue";
    private static final String ACTIVE_TOKEN = "active";
    private static final int MAX_QUEUE_SIZE = 100;
    private static final int MAX_ACTIVE_MINUTES = 5;

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private QueueService queueService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private UserDomain user1;
    private UserDomain user2;
    private UserDomain user3;

    @BeforeEach
    void setUp() {
        // 사용자 및 토큰 생성
        user1 = UserDomain.builder().userId(1L).build();
        user2 = UserDomain.builder().userId(2L).build();
        user3 = UserDomain.builder().userId(3L).build();

        queueService.createToken(user1);
        queueService.createToken(user2);
        queueService.createToken(user3);
    }

    @Test
    void getQueueOrder() {
        String token = queueService.createToken(user1).getToken();

        QueueFacadeDto result = queueFacade.getQueueOrder(token);

        assertNotNull(result);
        assertEquals(token, result.getToken());
        assertTrue(result.getQueueCount() > 0);  // 대기열 순서가 양수여야 함
    }

    @Test
    void activateTokens() {
        // 토큰 활성화 전 상태 확인 (큐에 남아있는 토큰 확인)
        long initialQueueCount = queueService.getQueueOrder(queueService.createToken(user1).getToken());

        // 토큰 활성화
        queueFacade.activateTokens();

        // 활성화 후 상태 확인 (활성화된 토큰이 큐에서 제거된 후 큐 카운트 확인)
        long postActivationQueueCount = queueService.getQueueOrder(queueService.createToken(user1).getToken());

        assertTrue(postActivationQueueCount < initialQueueCount);
    }

    @Test
    void expireTokens() throws InterruptedException {
        String token1 = "expiredToken";
        String token2 = "waitingToken";

        // 만료된 토큰 생성 (만료 시간보다 이전)
        long expiredTimestamp = System.currentTimeMillis() - ((MAX_ACTIVE_MINUTES + 1) * 60 * 1000);
        String expiredTokenWithTimestamp = String.format("%s:%d", token1, expiredTimestamp);

        // 만료된 토큰을 Redis에 추가
        redisTemplate.opsForSet().add(ACTIVE_TOKEN, expiredTokenWithTimestamp);
        redisTemplate.opsForZSet().add(QUEUE, token1, expiredTimestamp);

        // 활성 토큰 생성 (현재 시간으로 생성)
        long activeTimestamp = System.currentTimeMillis();
        String activeTokenWithTimestamp = String.format("%s:%d", token2, activeTimestamp);

        // 활성 토큰을 Redis에 추가
        redisTemplate.opsForSet().add(ACTIVE_TOKEN, activeTokenWithTimestamp);
        redisTemplate.opsForZSet().add(QUEUE, token2, activeTimestamp);

        // 만료 실행
        queueFacade.expireTokens();

        // 만료 후 상태 확인 (만료된 토큰이 제거되고, 활성 토큰의 순서를 확인)
        Long queueOrderAfterExpiration = queueService.getQueueOrder(token2);

        // 활성 토큰의 순서가 0이 아니어야 함 (만료된 토큰은 제거됨)
        assertNotNull(queueOrderAfterExpiration);
        // 기존 set에 생성 된 3개의 토큰 + 현재 생성한 1개 토큰 = 4개 토큰이 존재해야함.
        assertEquals(4L, queueOrderAfterExpiration);
    }
}