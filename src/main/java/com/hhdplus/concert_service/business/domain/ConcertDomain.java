package com.hhdplus.concert_service.business.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ConcertDomain {
    private Long id;
    private Long concertId;
    private String title;
    private Long price;
    private LocalDateTime concertDate;
    private Long seatNo;
    private Long userId;
    private String status;
    private LocalDateTime validTime;
    private List<LocalDateTime> availableDates;
    private List<Long> availableSeats;
}
