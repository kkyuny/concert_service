package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.PaymentFacadeDto;
import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.event.PaymentEvent;
import com.hhdplus.concert_service.business.event.PaymentEventPublisher;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import com.hhdplus.concert_service.business.service.*;
import com.hhdplus.concert_service.interfaces.common.exception.BadRequestException;
import com.hhdplus.concert_service.interfaces.common.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    @Autowired
    PaymentService paymentService;

    @Autowired
    ConcertService concertService;

    @Autowired
    UserService userService;

    @Autowired
    QueueService queueService;

    @Autowired
    PaymentEventPublisher paymentEventPublisher;

    @Autowired
    PaymentMessageOutboxWriter paymentMessageOutboxWriter;

    @Autowired
    QueueRedisService queueRedisService;

    @Transactional
    public PaymentFacadeDto executePayment(String token, PaymentFacadeDto dto){
        // 예약 정보 조회
        Optional<ConcertDomain> reservationOpt = concertService.getUserReservation(PaymentFacadeDto.toConcertDomain(dto));

        if(reservationOpt.isEmpty())
            throw new BadRequestException("Reservation not exist");

        ConcertDomain reservation = reservationOpt.get();
        dto.setId(reservation.getId());

        if(reservation.getStatus().equals("waiting")){
            try {
                UserDomain user = userService.useAmountUser(dto.getUserId(), dto.getPrice());
                PaymentDomain paymentResult = paymentService.savePayment(PaymentFacadeDto.toDomain(dto));

                concertService.changeConcertReserveToFinish(reservation);
                // queueRedisService.expireToken(token);
                //paymentEventPublisher.savePaymentHistory(paymentResult);

                // 이벤트 발행
                PaymentEvent event = PaymentEvent.builder()
                        .userId(user.getUserId())      // 사용자 ID
                        .price(paymentResult.getAmount())      // 결제 금액
                        .build();

                paymentEventPublisher.sendEvent(event);

                return PaymentFacadeDto.builder()
                        .userId(paymentResult.getUserId())
                        .amount(paymentResult.getAmount())
                        .concertId(paymentResult.getConcertId())
                        .concertDate(paymentResult.getConcertDate())
                        .seatNo(paymentResult.getSeatNo())
                        .build();
            } catch (Exception e) {
                throw new InternalServerErrorException("Seat reservation failed");
            }
        } else {
            throw new BadRequestException("Status is not waiting");
        }
    }
}
