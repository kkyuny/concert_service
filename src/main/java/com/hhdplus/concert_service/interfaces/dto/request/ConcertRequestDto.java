package com.hhdplus.concert_service.interfaces.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertRequestDto {
    private Long concertId;
    private String title;
    private LocalDate concertDate;
    private String status;
    private Long seatNo;
    private Long price;
}
