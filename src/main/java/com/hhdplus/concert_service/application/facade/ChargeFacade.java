package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.ChargeFacadeDto;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChargeFacade {

    @Autowired
    UserService userService;

    public ChargeFacadeDto getUserAmount(ChargeFacadeDto dto) {
        UserDomain user = userService.findUserById(dto.getUserId());

        return ChargeFacadeDto.builder()
            .amount(user.getAmount())
            .build();
    }


    public ChargeFacadeDto chargeUserAmount(ChargeFacadeDto dto) {
        UserDomain user = userService.findUserById(dto.getUserId());

        return ChargeFacadeDto.builder()
            .amount(userService.chargeAmountUser(user, dto.getAmount()).getAmount())
            .build();
    }

    public ChargeFacadeDto useUserAmount(ChargeFacadeDto dto) {
        UserDomain userDomain = userService.useAmountUser(dto.getUserId(), dto.getAmount());

        if (userDomain == null) {
            throw new RuntimeException("Failed to use amount for user.");
        }

        return ChargeFacadeDto.builder()
                .amount(userDomain.getAmount())
                .build();
    }
}
