package com.hhdplus.concert_service.business.service;

import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    public UserDomain findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InvalidReqBodyException("USER_NOT_FOUND"));
    }

    public UserDomain chargeAmountUser(UserDomain user, Long amount) {
        user.chargePoint(amount);

        return userRepository.save(user);
    }

    public UserDomain useAmountUser(UserDomain user, Long price) {
        user.usePoint(price);

        return userRepository.save(user);
    }
}
