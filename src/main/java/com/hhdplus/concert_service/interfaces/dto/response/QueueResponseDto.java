package com.hhdplus.concert_service.interfaces.dto.response;

import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueResponseDto {
    private Long no;
    private Long userId;
    private String token;
    private String status;
    private Long queueCount;
    private LocalDateTime validDate;

    public static QueueResponseDto toResponse(QueueFacadeDto dto) {
        return QueueResponseDto.builder()
                .userId(dto.getUserId())
                .token(dto.getToken())
                .status(dto.getStatus())
                .no(dto.getNo())
                .queueCount(dto.getQueueCount())
                .validDate(dto.getValidDate())
                .build();
    }
}
