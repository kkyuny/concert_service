package com.hhdplus.concert_service.business.domain;

import com.hhdplus.concert_service.infrastructure.entity.User;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import lombok.*;

import java.util.Optional;

@Getter
@Builder
public class UserDomain {
    private Long userId;
    private Long amount;

    public void chargePoint(long amount) {
        if (amount <= 0)
            throw new InvalidReqBodyException("INVALID POINT VALUE");

        this.amount += amount;
    }

    public void usePoint(long price) {
        if (price <= 0)
            throw new InvalidReqBodyException("INVALID POINT VALUE");

        if (this.amount < price)
            throw new InvalidReqBodyException("NOT ENOUGH USE AMOUNT");

        this.amount -= price;
    }
}
