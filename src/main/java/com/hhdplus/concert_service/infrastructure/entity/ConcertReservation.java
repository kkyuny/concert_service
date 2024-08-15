package com.hhdplus.concert_service.infrastructure.entity;

import com.hhdplus.concert_service.business.domain.ConcertDomain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "concert_reservation", indexes = {
        @Index(name = "idx_concert_reservation_concert_id", columnList = "concertId"),
        @Index(name = "idx_concert_reservation_concert_date", columnList = "concertDate"),
        @Index(name = "idx_concert_reservation_concert_id_concert_date", columnList = "concertDate, concertId") // 복합 인덱스 추가
})
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

    public static List<ConcertReservation> toEntity(List<ConcertDomain> domains) {
        return domains.stream()
                .map(ConcertReservation::toEntity)
                .collect(Collectors.toList());
    }
}
