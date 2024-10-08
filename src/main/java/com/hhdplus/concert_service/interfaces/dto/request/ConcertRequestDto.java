package com.hhdplus.concert_service.interfaces.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertRequestDto {
    private Long concertId;
    private String title;
    private LocalDateTime concertDate;
    private String status;
    private Long seatNo;
    private Long price;
    private Long userId;
}
