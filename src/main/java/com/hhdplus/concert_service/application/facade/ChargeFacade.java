package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.ChargeFacadeDto;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChargeFacade {

    @Autowired
    UserService userService;

    public ChargeFacadeDto getUserAmount(ChargeFacadeDto dto) {
        UserDomain user = userService.findUserById(dto.getUserId());
        ChargeFacadeDto.builder()
            .amount(user.getAmount());

        return dto;
    }


    public ChargeFacadeDto chargeUserAmount(ChargeFacadeDto dto) {
        UserDomain user = userService.findUserById(dto.getUserId());

        ChargeFacadeDto.builder()
            .amount(userService.chargeAmountUser(user, dto.getAmount()).getAmount());

        return dto;
    }

    public ChargeFacadeDto useUserAmount(ChargeFacadeDto dto) {
        UserDomain user = userService.findUserById(dto.getUserId());

        ChargeFacadeDto.builder()
            .amount(userService.useAmountUser(user, dto.getAmount()).getAmount());

        return dto;
    }
}
