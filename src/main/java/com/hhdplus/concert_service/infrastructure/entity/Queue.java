package com.hhdplus.concert_service.infrastructure.entity;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
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
public class Queue {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Id
    private String token;
    private String status;
    private LocalDateTime validDate;

    public static QueueDomain toDomain(Queue entity) {
        return QueueDomain.builder()
                .no(entity.getNo())
                .token(entity.getToken())
                .status(entity.getStatus())
                .validDate(entity.getValidDate())
                .build();
    }

    public static Queue toEntity(QueueDomain domain) {
        Queue entity = new Queue();

        entity.no = domain.getNo();
        entity.token = domain.getToken();
        entity.status = domain.getStatus();
        entity.validDate = domain.getValidDate();

        return entity;
    }
}
