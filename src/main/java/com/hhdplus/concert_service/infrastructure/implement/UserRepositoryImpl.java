package com.hhdplus.concert_service.infrastructure.implement;

import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.infrastructure.entity.User;
import com.hhdplus.concert_service.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<UserDomain> findById(Long userId) {
        return jpaRepository.findById(userId).map(User::toDomain);
    }

    @Override
    public UserDomain save(UserDomain user) {
        return User.toDomain(jpaRepository.save(User.toEntity(user)));
    }

    @Override
    public Optional<UserDomain> findUserByIdWithPessimisticWrite(Long userId) {
        return jpaRepository.findUserByIdWithPessimisticWrite(userId).map(User::toDomain);
    }

}
