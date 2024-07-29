package com.hhdplus.concert_service.interfaces.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueRequestDto {
    private Long userId;
    private String token;
    private String status;
}
