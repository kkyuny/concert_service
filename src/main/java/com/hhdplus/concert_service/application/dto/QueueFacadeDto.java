package com.hhdplus.concert_service.application.dto;

import com.hhdplus.concert_service.interfaces.dto.request.ChargeRequestDto;
import com.hhdplus.concert_service.interfaces.dto.request.QueueRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class QueueFacadeDto {
    private Long userId;
    private String token;
    private String status;
    private Long queueCount;
    private LocalDateTime validDate;

    public static QueueFacadeDto toFacadeDto(QueueRequestDto dto) {
        return QueueFacadeDto.builder()
                .userId(dto.getUserId())
                .token(dto.getToken())
                .status(dto.getStatus())
                .build();
    }
}
