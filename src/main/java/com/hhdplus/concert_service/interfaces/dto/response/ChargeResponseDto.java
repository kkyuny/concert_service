package com.hhdplus.concert_service.interfaces.dto.response;

import com.hhdplus.concert_service.application.dto.ChargeFacadeDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeResponseDto {
    private Long userId;
    private Long amount;

    public static ChargeResponseDto toResponseDto(ChargeFacadeDto dto) {
        return ChargeResponseDto.builder()
                .userId(dto.getUserId())
                .amount(dto.getAmount())
                .build();
    }
}
