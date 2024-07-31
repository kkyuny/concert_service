package com.hhdplus.concert_service.infrastructure.entity;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
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
public class ConcertSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertId;
    private Long price;
    private LocalDateTime concertDate;
    private LocalDateTime regiDate;

    public static ConcertDomain toDomain(ConcertSchedule entity) {
        return ConcertDomain.builder()
                .concertId(entity.getConcertId())
                .price(entity.getPrice())
                .concertDate(entity.getConcertDate())
                .build();
    }

    public static ConcertSchedule toEntity(ConcertDomain domain) {
        ConcertSchedule entity = new ConcertSchedule();

        entity.concertId = domain.getConcertId();
        entity.price = domain.getPrice();
        entity.concertDate = domain.getConcertDate();

        return entity;
    }
}
