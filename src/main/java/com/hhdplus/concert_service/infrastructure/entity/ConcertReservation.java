package com.hhdplus.concert_service.infrastructure.entity;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import jakarta.persistence.*;
import lombok.*;
import org.h2.schema.Domain;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long concertId;
    private LocalDateTime concertDate;
    private Long seatNo;
    private Long userId;
    private String status;
    private LocalDateTime validDate;
    private LocalDateTime regiDate;
    @Version
    private int version;

    public static ConcertDomain toDomain(ConcertReservation entity) {
        return ConcertDomain.builder()
                .id(entity.getId())
                .concertId(entity.getConcertId())
                .seatNo(entity.getSeatNo())
                .status(entity.getStatus())
                .userId(entity.getUserId())
                .concertDate(entity.getConcertDate())
                .validTime(entity.getValidDate())
                .build();
    }

    public static ConcertReservation toEntity(ConcertDomain domain) {
        ConcertReservation entity = new ConcertReservation();
        entity.id = domain.getId();
        entity.concertId = domain.getConcertId();
        entity.seatNo = domain.getSeatNo();
        entity.status = domain.getStatus();
        entity.concertDate = domain.getConcertDate();
        entity.userId = domain.getUserId();
        entity.validDate = domain.getValidTime();

        return entity;
    }
}
