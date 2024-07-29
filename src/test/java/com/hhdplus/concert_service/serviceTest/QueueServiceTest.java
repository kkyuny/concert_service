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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

        // `findById` 메서드 호출 시 `queueDomain` 반환하도록 설정
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
}
