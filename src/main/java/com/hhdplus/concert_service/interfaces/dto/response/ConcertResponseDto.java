package com.hhdplus.concert_service.interfaces.dto.response;

import com.hhdplus.concert_service.application.dto.ChargeFacadeDto;
import com.hhdplus.concert_service.application.dto.ConcertFacadeDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConcertResponseDto {
    private Long concertId;
    private String title;
    private Long price;
    private String status;
    private Long seatNo;
    private List<LocalDateTime> availableDates;
    private List<Long> availableSeats;

    public static ConcertResponseDto toResponse(ConcertFacadeDto dto) {
        return ConcertResponseDto.builder()
                .concertId(dto.getConcertId())
                .title(dto.getTitle())
                .price(dto.getPrice())
                .seatNo(dto.getSeatNo())
                .status(dto.getStatus())
                .availableDates(dto.getAvailableDates())
                .availableSeats(dto.getAvailableSeats())
                .build();
    }
}
