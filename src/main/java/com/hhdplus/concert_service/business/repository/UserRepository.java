package com.hhdplus.concert_service.business.repository;

import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.infrastructure.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<UserDomain> findById(Long userId);

    UserDomain save(UserDomain user);
}
