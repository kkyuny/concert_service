package com.hhdplus.concert_service.business.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentDomain {
    private Long userId;
    private Long concertId;
    private LocalDateTime concertDate;
    private Long seatNo;
    private Long price;
    private Long amount;
}
