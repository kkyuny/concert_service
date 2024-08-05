package com.hhdplus.concert_service.interfaces.scheduler;


import com.hhdplus.concert_service.application.facade.ConcertFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertScheduler {
    @Autowired
    private ConcertFacade concertFacade;

    @Scheduled(fixedDelay = 5000)
    public void cancelReservation() {
        concertFacade.cancelReservation();
    }
}
