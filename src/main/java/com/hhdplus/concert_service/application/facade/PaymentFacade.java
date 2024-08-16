package com.hhdplus.concert_service.application.facade;

import com.hhdplus.concert_service.application.dto.PaymentFacadeDto;
import com.hhdplus.concert_service.business.domain.ConcertDomain;
import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.event.PaymentEvent;
import com.hhdplus.concert_service.business.event.PaymentEventPublisher;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import com.hhdplus.concert_service.business.message.PaymentMessageSender;
import com.hhdplus.concert_service.business.service.ConcertService;
import com.hhdplus.concert_service.business.service.PaymentService;
import com.hhdplus.concert_service.business.service.QueueService;
import com.hhdplus.concert_service.business.service.UserService;
import com.hhdplus.concert_service.infrastructure.entity.ConcertReservation;
import com.hhdplus.concert_service.infrastructure.entity.PaymentOutbox;
import com.hhdplus.concert_service.infrastructure.repository.PaymentOutboxJpaRepository;
import com.hhdplus.concert_service.interfaces.common.exception.BadRequestException;
import com.hhdplus.concert_service.interfaces.common.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public PaymentFacadeDto executePayment(PaymentFacadeDto dto){
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
                Optional<QueueDomain> queue = queueService.findQueueByUserId(user.getUserId());
                if (queue.isEmpty()) {
                    throw new BadRequestException("Token not found");
                }
                String token = queue.get().getToken();
                queueService.deleteQueue(token);
                //paymentEventPublisher.savePaymentHistory(paymentResult);

                // outbox 메세지 저장
                PaymentMessage message = PaymentMessage.builder()
                        .userId(user.getUserId())      // 사용자 ID
                        .price(paymentResult.getAmount())      // 결제 금액
                        .status("INIT")                      // 상태
                        .build();

                PaymentOutbox saveMessage = paymentMessageOutboxWriter.save(message);

                // 이벤트 발행(인프라) -> 이벤트 수신(인터페이스) -> kakfa에서 메세지 send(인프라).
                PaymentEvent event = PaymentEvent.builder()
                        .id(saveMessage.getId())
                        .userId(user.getUserId())      // 사용자 ID
                        .price(paymentResult.getAmount())      // 결제 금액
                        .status("INIT")                      // 상태
                        .build();

                paymentEventPublisher.sendMessage(event);

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
