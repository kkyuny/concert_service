package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.service.QueueService;
import com.hhdplus.concert_service.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueFacade {

    @Autowired
    QueueService queueService;

    @Autowired
    UserService userService;

    public QueueFacadeDto createToken(QueueFacadeDto dto) {
        UserDomain user = userService.findUserById(dto.getUserId());
        QueueDomain queue = queueService.createToken(user);

        QueueFacadeDto.builder()
                .token(queue.getToken())
                .status(queue.getStatus())
                .validDate(queue.getValidDate())
                .build();

        return dto;
    }

    public QueueFacadeDto checkQueue(QueueFacadeDto dto) {
        QueueDomain queue = queueService.findTokenById(dto.getToken());

        if(queueService.checkQueue(queue) && "waiting".equals(queue.getStatus())){
            QueueDomain changeResult = queueService.changeStatusToActive(queue);

            QueueFacadeDto.builder()
                    .queueCount(queueService.getActiveUserCount(queue).getQueueCount())
                    .status(changeResult.getStatus())
                    .validDate(changeResult.getValidDate())
                    .build();
        } else {
            QueueFacadeDto.builder()
                .queueCount(queueService.getActiveUserCount(queue).getQueueCount())
                .status(queue.getStatus())
                .validDate(queue.getValidDate())
                .build();
        }

        return dto;
    }
}
