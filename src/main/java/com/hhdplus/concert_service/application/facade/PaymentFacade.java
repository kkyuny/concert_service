package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.PaymentFacadeDto;
import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.service.ConcertService;
import com.hhdplus.concert_service.business.service.PaymentService;
import com.hhdplus.concert_service.business.service.QueueService;
import com.hhdplus.concert_service.business.service.UserService;
import com.hhdplus.concert_service.infrastructure.entity.User;
import com.hhdplus.concert_service.interfaces.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PaymentFacade {

    @Autowired
    PaymentService paymentService;

    @Autowired
    ConcertService concertService;

    @Autowired
    UserService userService;

    @Autowired
    QueueService queueService;

    public PaymentFacadeDto executePayment(PaymentFacadeDto dto){
        Optional<ConcertDomain> reservation = Optional.ofNullable(concertService.getUserReservation(PaymentFacadeDto.toConcertDomain(dto)));

        if(reservation.isEmpty())
            throw new BadRequestException("예약 정보 없음");

        if(reservation.get().getStatus().equals("waiting")){
            UserDomain user = userService.findUserById(reservation.get().getUserId());

            userService.useAmountUser(user, dto.getPrice());
            paymentService.savePayment(PaymentFacadeDto.toDomain(dto));
            concertService.changeConcertReserveToFinish(reservation.get());

            queueService.deleteQueue(queueService.findTokenByUserId(user.getUserId()).getToken());
            // 체크 토큰 한번 더(스케쥴러에 의한 상태변화 방지)

        } else {
            throw new BadRequestException("Status is not waiting");
        }

        return dto;
    }
}
