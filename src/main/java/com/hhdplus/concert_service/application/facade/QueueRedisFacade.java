package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.service.QueueRedisService;
import com.hhdplus.concert_service.business.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueRedisFacade {
    @Autowired
    QueueRedisService queueRedisService;

    @Autowired
    UserService userService;

    public QueueFacadeDto createToken(QueueFacadeDto facadeDto) {
        UserDomain user = userService.findUserById(facadeDto.getUserId());
        QueueDomain queue = QueueDomain.builder()
                .userId(user.getUserId())
                .build();

        queue.create();

        String token = queue.getToken();
        queueRedisService.addQueue(token);

        return QueueFacadeDto.builder().token(token).build();
    }

    public QueueFacadeDto getQueueOrder(String token) {
        Long rank = queueRedisService.getQueueOrder(token);

        return QueueFacadeDto.builder().queueCount(rank).build();
    }

    public Boolean verifyToken(String token) {
        return queueRedisService.verifyToken(token);
    }
}
