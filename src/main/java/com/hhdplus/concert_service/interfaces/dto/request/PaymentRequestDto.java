package com.hhdplus.concert_service.interfaces.dto.request;

import com.hhdplus.concert_service.application.dto.PaymentFacadeDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {
    private Long userId;
    private Long concertId;
    private LocalDateTime concertDate;
    private Long seatNo;
    private Long price;
}
