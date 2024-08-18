package com.hhdplus.concert_service.serviceTest;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.QueueRepository;
import com.hhdplus.concert_service.business.service.QueueService;
import com.hhdplus.concert_service.infrastructure.entity.Queue;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QueueServiceTest {
    private static final int MAX_ACTIVE_USERS = 100;
    private static final int MAX_ACTIVE_MINUTES = 5;

    @Mock
    QueueRepository queueRepository;

    @InjectMocks
    QueueService queueService;

    private UserDomain user;
    private QueueDomain queue;
    private String validToken;

    @BeforeEach
    void setUp() {
        user = UserDomain.builder().userId(1L).build();
        queue = QueueDomain.builder().userId(user.getUserId()).build();
        queue.create();
        validToken = queue.getToken(); // 유효한 토큰을 가져옴
    }

    @Test
    @DisplayName("토큰 생성")
    void addQueue() {
        //when
        QueueDomain queue = queueService.createToken(user);

        //then
        Assertions.assertThat(queue.getToken()).isNotNull();
    }

    @Test
    @DisplayName("토큰 검증 테스트")
    void verifyToken() {
        String validToken = queue.getToken(); // 가정: getToken() 메서드가 토큰을 반환

        when(queueRepository.verifyToken(validToken)).thenReturn(true);

        assertDoesNotThrow(() -> queueService.verifyToken(validToken));
    }

    @Test
    @DisplayName("토큰 검증 실패")
    void verifyToken_failure() {
        String invalidToken = "invalidToken";

        when(queueRepository.verifyToken(invalidToken)).thenReturn(false);

        InvalidReqBodyException exception = assertThrows(InvalidReqBodyException.class, () -> {
            queueService.verifyToken(invalidToken);
        });

        assertEquals("INVALID_TOKEN", exception.getMessage());
    }

    @Test
    @DisplayName("대기열 순서 조회")
    void getQueueOrder() {
        //when
        when(queueRepository.getQueueOrder(validToken)).thenReturn(1L);

        long queueOrder = queueService.getQueueOrder(validToken);

        assertEquals(1L, queueOrder);
        verify(queueRepository, times(1)).getQueueOrder(validToken);
    }

    @Test
    @DisplayName("토큰 만료")
    void expireToken_success() {
        doNothing().when(queueRepository).expireToken(validToken);

        queueService.expireToken(validToken);

        verify(queueRepository, times(1)).expireToken(validToken);
    }

    @Test
    @DisplayName("토큰s 만료")
    void expireTokens_success() {
        doNothing().when(queueRepository).expireTokens();

        queueService.expireTokens();

        verify(queueRepository, times(1)).expireTokens();
    }

    @Test
    @DisplayName("토큰s 활성")
    void activateTokens_success() {
        doNothing().when(queueRepository).expireTokens();

        queueService.expireTokens();

        verify(queueRepository, times(1)).expireTokens();
    }
}
