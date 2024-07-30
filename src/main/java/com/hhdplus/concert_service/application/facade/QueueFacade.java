package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.service.QueueService;
import com.hhdplus.concert_service.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
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

    public QueueFacadeDto checkQueue(QueueFacadeDto dto) {
        QueueDomain queue = queueService.findTokenById(dto.getToken());

        if(queueService.checkQueue(queue) && "waiting".equals(queue.getStatus())){
            QueueDomain changeResult = queueService.changeStatusToActive(queue);

            return QueueFacadeDto.builder()
                    .queueCount(queueService.getActiveUserCount(queue).getQueueCount())
                    .status(changeResult.getStatus())
                    .validDate(changeResult.getValidDate())
                    .build();
        } else {
            return QueueFacadeDto.builder()
                .queueCount(queueService.getActiveUserCount(queue).getQueueCount())
                .status(queue.getStatus())
                .validDate(queue.getValidDate())
                .build();
        }
    }

    public QueueFacadeDto getQueueOrder(QueueFacadeDto dto) {
        QueueDomain queue = queueService.findTokenById(dto.getToken());

        Long activeUserCount = queueService.getActiveUserCount(queue).getQueueCount();
        if(activeUserCount < MAX_ACTIVE_USERS){
            return QueueFacadeDto.builder()
                    .queueCount(0L)
                    .build();
        } else {
            Long waitingUserCountAfterMe = queueService.getWaitingUserCountBeforeMe(queue).getQueueCount();

            return QueueFacadeDto.builder()
                    .queueCount(waitingUserCountAfterMe)
                    .build();
        }
    }

    public void activateTokens() {
        Long activeCount = queueService.getActiveUserCount().getQueueCount();

        // 최대 활성 사용자 수를 초과하지 않도록 대기 중인 사용자 중 활성화할 수 있는 사용자를 찾는다.
        long availableCount  = MAX_ACTIVE_USERS - activeCount;
        List<QueueDomain> waitingQueues = queueService.getWaitingUserCountToActive(availableCount);

        // 활성 사용자 수가 최대 수치를 초과하지 않도록 대기 중인 사용자를 활성화
        long activeUserCount = 0L;
        for (QueueDomain queue : waitingQueues) {
            if (activeUserCount > availableCount) {
                break;
            }
            queue.setStatus("active");
            queueService.changeStatusToActive(queue);
            activeUserCount++;
        }
    }

    public void expireTokens() {
        LocalDateTime now = LocalDateTime.now();

        List<QueueDomain> activeQueues = queueService.getActiveQueues();

        for (QueueDomain queue : activeQueues) {
            if (queue.getValidDate().isBefore(now)) {
                // 유효 시간이 지난 토큰 삭제
                queueService.deleteQueue(queue.getToken());
            }
        }
    }
}
