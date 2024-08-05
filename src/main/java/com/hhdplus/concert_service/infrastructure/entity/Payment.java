package com.hhdplus.concert_service.infrastructure.entity;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.domain.PaymentDomain;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long concertId;
    private LocalDateTime concertDate;
    private Long amount;
    private Long seatNo;
    private LocalDateTime regiDate;

    public static PaymentDomain toDomain(Payment entity) {
        return PaymentDomain.builder()
                .id(entity.id)
                .userId(entity.userId)
                .concertId(entity.getConcertId())
                .concertDate(entity.getConcertDate())
                .amount(entity.getAmount())
                .seatNo(entity.seatNo)
                .build();
    }

    public static Payment toEntity(PaymentDomain domain) {
        Payment entity = new Payment();

        entity.id = domain.getId();
        entity.userId = domain.getUserId();
        entity.concertId = domain.getConcertId();
        entity.concertDate = domain.getConcertDate();
        entity.amount = domain.getAmount();
        entity.seatNo = domain.getSeatNo();

        return entity;
    }
}
