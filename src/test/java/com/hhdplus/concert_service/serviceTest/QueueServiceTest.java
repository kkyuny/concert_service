package com.hhdplus.concert_service.serviceTest;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.QueueRepository;
import com.hhdplus.concert_service.business.service.QueueService;
import com.hhdplus.concert_service.infrastructure.entity.Queue;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QueueServiceTest {
    private static final int MAX_ACTIVE_USERS = 100;
    private static final int MAX_ACTIVE_MINUTES = 5;

    @Mock
    QueueRepository queueRepository;

    @InjectMocks
    QueueService queueService;

    @DisplayName("토큰 생성 테스트")
    @Test
    void createTokenTest() {
        //given
        long userId = 1L;
        UserDomain user = UserDomain.builder().userId(userId).build();

        QueueDomain queueDomain = QueueDomain.builder()
                .userId(user.getUserId())
                .build();
        queueDomain.create();  // create 메서드를 호출하여 UUID 생성

        when(queueRepository.save(any(QueueDomain.class))).thenReturn(queueDomain);
        when(queueRepository.findById(queueDomain.getToken())).thenReturn(Optional.of(queueDomain));

        //when
        QueueDomain createdQueue = queueService.createToken(user); // ??

        //then
        assertThat(queueService.findTokenById(createdQueue.getToken()).getToken()).isEqualTo(queueDomain.getToken());
    }

    @DisplayName("토큰 조회 테스트")
    @Test
    void findTokenByIdTest() {
        // given
        QueueDomain queueDomain = QueueDomain.builder()
                .build();
        queueDomain.create();

        when(queueRepository.findById(queueDomain.getToken())).thenReturn(Optional.of(queueDomain));

        // when
        QueueDomain result = queueService.findTokenById(queueDomain.getToken());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(queueDomain.getToken());
        assertThat(result.getStatus()).isEqualTo("waiting");
    }

    @DisplayName("토큰 조회 실패 테스트")
    @Test
    void findTokenByIdFailTest() {
        // given
        String token = UUID.randomUUID().toString().replace("-", "");

        when(queueRepository.findById(token)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> queueService.findTokenById(token))
                .isInstanceOf(InvalidReqBodyException.class)
                .hasMessage("INVALID_TOKEN");
    }

    @Test
    @DisplayName("active 유저 조회 테스트")
    void getQueueOrder() {
        //given
        long userId = 1L;
        String token = "token";

        List<QueueDomain> activeQueues = Arrays.asList(
                QueueDomain.builder().token("1").status("active").build(),
                QueueDomain.builder().token("2").status("active").build(),
                // QueueDomain.builder().no(3L).status("waiting").build(),
                QueueDomain.builder().token("3").status("active").build(),
                QueueDomain.builder().token("4").status("active").build(),
                QueueDomain.builder().token("5").status("active").build()

        );

        QueueDomain queueDomain = QueueDomain.builder().token("7").build();

        when(queueRepository.findActiveQueues(queueDomain.getToken())).thenReturn(activeQueues);

        // when
        QueueDomain result = queueService.getActiveUserCount(queueDomain);

        // then
        Assertions.assertThat(result.getQueueCount()).isEqualTo(5L); // 활성 유저 수
    }

    @Test
    @DisplayName("대기 유저 수 조회 테스트 (나보다 앞서 있는 대기 유저 수)")
    void getWaitingUserCountBeforeMeTest() {
        // given
        List<QueueDomain> waitingQueues = Arrays.asList(
                QueueDomain.builder().token("1").status("waiting").build(),
                QueueDomain.builder().token("2").status("waiting").build(),
                QueueDomain.builder().token("3").status("waiting").build(),
                QueueDomain.builder().token("4").status("waiting").build()
        );

        QueueDomain queueDomain = QueueDomain.builder().token("5").build();

        // Mock 설정
        when(queueRepository.findWaitingQueuesBeforeMe("5")).thenReturn(waitingQueues);

        // when
        QueueDomain result = queueService.getWaitingUserCountBeforeMe(queueDomain);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getQueueCount()).isEqualTo(4L); // 대기 유저 수
    }

    @Test
    @DisplayName("대기 중인 유저 조회 테스트")
    void getWaitingUserCountToActivateTest() {
        // given
        long availableCount = 3L;

        List<QueueDomain> expectedWaitingUsers = Arrays.asList(
                QueueDomain.builder().token("1").status("waiting").build(),
                QueueDomain.builder().token("2").status("waiting").build(),
                QueueDomain.builder().token("3").status("waiting").build()
        );
        when(queueRepository.findWaitingUserCountToActive(availableCount)).thenReturn(expectedWaitingUsers);

        // when
        List<QueueDomain> actualWaitingUsers = queueService.getWaitingUserCountToActive(availableCount);

        // then
        assertThat(actualWaitingUsers.size()).isEqualTo(expectedWaitingUsers.size());
    }

    @Test
    @DisplayName("토큰 만료 테스트")
    void deleteQueue_ShouldCallDeleteById() {
        // Given
        String token = "test-token";

        // When
        queueService.deleteQueue(token);

        // Then
        verify(queueRepository, times(1)).deleteById(token);
    }
}
