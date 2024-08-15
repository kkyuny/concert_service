package com.hhdplus.concert_service.business.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentMessage {
    private Long id;
    private Long userId;
    private Long price;
    private String status;
}
