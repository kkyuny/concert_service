package com.hhdplus.concert_service.infrastructure.entity;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "queue", indexes = {
    @Index(name = "idx_queue_status", columnList = "status")
})
public class Queue {
    @Id
    @Column(name = "token")
    private String token;
    @Column(name = "status")
    private String status;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "valid_date")
    private LocalDateTime validDate;
    @Column(name = "regi_date")
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

    public static List<Queue> toEntity(List<QueueDomain> domains) {
        return domains.stream()
                .map(Queue::toEntity)
                .collect(Collectors.toList());
    }
}
