package com.hhdplus.concert_service.business.service;

import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class UserService {

    static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserDomain findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InvalidReqBodyException("USER_NOT_FOUND"));
    }

    public UserDomain chargeAmountUser(UserDomain user, Long amount) {
        user.chargePoint(amount);

        try{
            return userRepository.save(user);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("Amount charge error", e);

            return null;
        }
    }

    public UserDomain useAmountUser(Long userId, Long price) {
        // 비관적 잠금을 사용하여 사용자 엔티티 조회
        UserDomain user = userRepository.findUserByIdWithPessimisticWrite(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.usePoint(price);

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error("Amount use error", e);

            return null;
        }
    }

    public UserDomain findUserByIdWithPessimisticWrite(Long userId) {
        return userRepository.findUserByIdWithPessimisticWrite(userId)
                .orElseThrow(() -> new InvalidReqBodyException("USER_NOT_FOUND"));
    }
}
