package com.hhdplus.concert_service.application.dto;

import com.hhdplus.concert_service.interfaces.dto.request.ConcertRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ConcertFacadeDto {
    private Long concertId;
    private String title;
    private Long price;
    private LocalDate concertDate;
    private String status;
    private Long seatNo;
    private List<LocalDate> availableDates;
    private List<Long> availableSeats;

    public static ConcertFacadeDto toFacadeDto(ConcertRequestDto dto) {
        return ConcertFacadeDto.builder()
                .concertId(dto.getConcertId())
                .title(dto.getTitle())
                .price(dto.getPrice())
                .seatNo(dto.getSeatNo())
                .status(dto.getStatus())
                .concertDate(dto.getConcertDate())
                .build();
    }
}
