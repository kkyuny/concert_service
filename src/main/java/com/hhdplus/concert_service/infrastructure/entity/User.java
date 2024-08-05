package com.hhdplus.concert_service.infrastructure.entity;

import com.hhdplus.concert_service.business.domain.UserDomain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
@Setter
@Builder
@Table(name = "`User`")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ColumnDefault("0")
    private Long amount;
    private LocalDateTime regiDate;

    public static UserDomain toDomain(User entity) {
        return UserDomain.builder()
                .userId(entity.getId())
                .amount(entity.getAmount())
                .build();
    }

    public static User toEntity(UserDomain domain) {
        User entity = new User();
        entity.id = domain.getUserId();
        entity.amount = domain.getAmount();

        return entity;
    }
}
