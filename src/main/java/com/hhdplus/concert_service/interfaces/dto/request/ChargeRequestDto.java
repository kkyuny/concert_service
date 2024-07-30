package com.hhdplus.concert_service.interfaces.dto.request;

import com.hhdplus.concert_service.application.dto.ChargeFacadeDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeRequestDto {
    private Long userId;
    private Long amount;
}
