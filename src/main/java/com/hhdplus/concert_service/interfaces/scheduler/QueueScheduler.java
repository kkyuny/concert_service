package com.hhdplus.concert_service.interfaces.scheduler;

import com.hhdplus.concert_service.application.facade.QueueFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueScheduler {
    @Autowired
    QueueFacade queueFacade;

    @Scheduled(fixedDelay = 5 * 1000)
    public void activateTokens() {
        queueFacade.activateTokens();
    }

    @Scheduled(fixedDelay = 10 * 1000)
    public void expireTokens() {
        queueFacade.expireTokens();
    }
}
