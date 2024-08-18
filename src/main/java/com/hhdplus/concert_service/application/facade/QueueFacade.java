package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.service.QueueService;
import com.hhdplus.concert_service.business.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueFacade {
    private static final int MAX_ACTIVE_USERS = 100;
    private static final int MAX_ACTIVE_MINUTES = 5;

    @Autowired
    QueueService queueService;

    @Autowired
    UserService userService;

    public QueueFacadeDto createToken(QueueFacadeDto dto) {
        UserDomain user = userService.findUserById(dto.getUserId());
        QueueDomain queue = queueService.createToken(user);

        return QueueFacadeDto.builder()
                .token(queue.getToken())
                .status(queue.getStatus())
                .validDate(queue.getValidDate())
                .build();
    }

    // 대기열 순서 조회
    public QueueFacadeDto getQueueOrder(String token) {
        long queueOrder = queueService.getQueueOrder(token);

        return QueueFacadeDto.builder()
                .token(token)
                .queueCount(queueOrder)
                .build();
    }

    // 토큰 검증
    public void verifyToken(String token) {
        queueService.verifyToken(token);
    }

    // 토큰 활성화
    public void activateTokens() {
        queueService.activateTokens();
    }

    // 토큰 만료
    public void expireTokens() {
        queueService.expireTokens();
    }
}
