package com.hhdplus.concert_service.infrastructure.repository;

import com.hhdplus.concert_service.infrastructure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

}
