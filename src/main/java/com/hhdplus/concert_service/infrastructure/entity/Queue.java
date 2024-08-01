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
    @Id
    private String token;
    private String status;
    private Long userId;
    private LocalDateTime validDate;
    private LocalDateTime regiDate;

    public static QueueDomain toDomain(Queue entity) {
        return QueueDomain.builder()
                .token(entity.getToken())
                .userId(entity.userId)
                .status(entity.getStatus())
                .validDate(entity.getValidDate())
                .build();
    }

    public static Queue toEntity(QueueDomain domain) {
        Queue entity = new Queue();

        entity.token = domain.getToken();
        entity.status = domain.getStatus();
        entity.userId = domain.getUserId();
        entity.validDate = domain.getValidDate();

        return entity;
    }
}
