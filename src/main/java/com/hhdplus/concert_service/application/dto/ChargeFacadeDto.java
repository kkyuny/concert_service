package com.hhdplus.concert_service.application.dto;

import com.hhdplus.concert_service.interfaces.dto.request.ChargeRequestDto;
import com.hhdplus.concert_service.interfaces.dto.response.ChargeResponseDto;
import lombok.*;

@Getter
@Setter
@Builder
public class ChargeFacadeDto {
    private Long userId;
    private Long amount;

    public static ChargeFacadeDto toFacadeDto(ChargeRequestDto dto) {
        return ChargeFacadeDto.builder()
                .userId(dto.getUserId())
                .amount(dto.getAmount())
                .build();
    }
}
