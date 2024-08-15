package com.hhdplus.concert_service.business.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentEvent {
    private Long userId;
    private Long price;
}
