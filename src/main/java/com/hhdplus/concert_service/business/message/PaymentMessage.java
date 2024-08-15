package com.hhdplus.concert_service.business.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMessage {
    @JsonProperty("id")
    private Long id;
    private Long userId;
    private Long price;
    private String status;
}
