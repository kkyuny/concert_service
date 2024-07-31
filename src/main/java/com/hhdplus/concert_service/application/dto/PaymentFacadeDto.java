package com.hhdplus.concert_service.application.dto;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.interfaces.dto.request.PaymentRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentFacadeDto {
    private Long userId;
    private Long concertId;
    private LocalDateTime concertDate;
    private Long seatNo;
    private Long price;
    private Long amount;

    public static PaymentFacadeDto toFacadeDto(PaymentRequestDto dto){
        return PaymentFacadeDto.builder()
                .userId(dto.getUserId())
                .concertId(dto.getConcertId())
                .concertDate(dto.getConcertDate())
                .seatNo(dto.getSeatNo())
                .price(dto.getPrice())
                .build();
    }

    public static PaymentDomain toDomain(PaymentFacadeDto dto) {
        return PaymentDomain.builder()
                .userId(dto.getUserId())
                .concertId(dto.getConcertId())
                .concertDate(dto.getConcertDate())
                .amount(dto.getAmount())
                .build();
    }

    public static ConcertDomain toConcertDomain(PaymentFacadeDto dto) {
        return ConcertDomain.builder()
                .userId(dto.getUserId())
                .concertId(dto.getConcertId())
                .concertDate(dto.getConcertDate())
                .seatNo(dto.getSeatNo())
                .build();
    }
}
