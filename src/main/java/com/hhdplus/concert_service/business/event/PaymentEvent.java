package com.hhdplus.concert_service.business.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private Long id;
    private Long userId;
    private Long paymentId;
    private Long price;
    private String status;
}
