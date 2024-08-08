package com.hhdplus.concert_service.business.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class QueueDomain {
    private static final int MAX_ACTIVE_USERS = 100;
    private static final int MAX_ACTIVE_MINUTES = 5;

    private Long userId;
    private String status;
    private String token;
    private LocalDateTime validDate;
    private LocalDateTime regiDate;
    private Long queueCount;

    public void create(){
        this.token = UUID.randomUUID().toString().replace("-", "");
        this.status = "waiting";
        this.validDate = LocalDateTime.now().plusMinutes(MAX_ACTIVE_MINUTES);
    }
}
