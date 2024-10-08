package com.hhdplus.concert_service.interfaces.controller;

import com.hhdplus.concert_service.application.dto.ConcertFacadeDto;
import com.hhdplus.concert_service.application.facade.ConcertFacade;
import com.hhdplus.concert_service.interfaces.dto.request.ConcertRequestDto;
import com.hhdplus.concert_service.interfaces.dto.response.ConcertResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/concert")
@Tag(name = "콘서트", description = "콘서트 조회 및 좌석 예약 API")
public class ConcertController {

    @Autowired
    ConcertFacade concertFacade;

    @GetMapping("/dates")
    @Operation(summary = "예약가능 날짜 조회")
    public ConcertResponseDto getAvailableDates(@RequestBody ConcertRequestDto dto) {
        return ConcertResponseDto.toResponse(concertFacade.getAvailableDates(ConcertFacadeDto.toFacadeDto(dto)));
    }

    @GetMapping("/seats")
    @Operation(summary = "예약가능 좌석 조회")
    public ConcertResponseDto getAvailableSeats(@RequestBody ConcertRequestDto dto) {
        return ConcertResponseDto.toResponse(concertFacade.getAvailableSeats(ConcertFacadeDto.toFacadeDto(dto)));
    }

    @PostMapping("/reserve")
    @Operation(summary = "좌석 예약")
    public ConcertResponseDto reserveSeat(@RequestBody ConcertRequestDto dto) {
        return ConcertResponseDto.toResponse(concertFacade.reserveSeat(ConcertFacadeDto.toFacadeDto(dto)));
    }
}
