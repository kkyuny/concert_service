package com.hhdplus.concert_service.serviceTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.event.PaymentEvent;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import com.hhdplus.concert_service.business.message.PaymentMessageSender;
import com.hhdplus.concert_service.infrastructure.entity.Payment;
import com.hhdplus.concert_service.infrastructure.entity.PaymentOutbox;
import com.hhdplus.concert_service.infrastructure.implement.PaymentOutboxRepositoryImpl;
import com.hhdplus.concert_service.infrastructure.repository.PaymentOutboxJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentEventTest {
    @Mock
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @InjectMocks
    private PaymentOutboxRepositoryImpl paymentOutboxRepository;

    private UserDomain user;

    @BeforeEach
    void setUp() {
        user = UserDomain.builder().userId(1L).build();
    }

    @Test
    @DisplayName("메시지 발행 전 Outbox 저장")
    void savePaymentOutbox() throws JsonProcessingException {
        //given
        PaymentDomain payment = PaymentDomain.builder().id(1L).build();
        PaymentMessage message = PaymentMessage.builder()
                .userId(user.getUserId())
                .paymentId(payment.getId())
                .status("INIT")
                .build();

        //when
        paymentOutboxRepository.save(message);

        //then
        verify(paymentOutboxJpaRepository, times(1)).save(any(PaymentOutbox.class));
    }

    @Test
    @DisplayName("발행 된 Outbox 발행 처리")
    void completePaymentOutbox() throws JsonProcessingException {
        //given
        PaymentDomain payment = PaymentDomain.builder().id(1L).build();
        PaymentMessage message = PaymentMessage.builder()
                .userId(user.getUserId())
                .paymentId(payment.getId())
                .status("INIT")
                .build();

        PaymentOutbox existingOutbox = new PaymentOutbox();
        existingOutbox.setId(message.getPaymentId());
        existingOutbox.setPaymentId(message.getPaymentId());
        existingOutbox.setStatus("INIT");
        existingOutbox.setCreateDate(LocalDateTime.now());

        when(paymentOutboxJpaRepository.findByPaymentId(payment.getId()))
                .thenReturn(Optional.of(existingOutbox));

        when(paymentOutboxJpaRepository.save(any(PaymentOutbox.class)))
                .thenReturn(existingOutbox);

        PaymentOutbox updatedOutbox = paymentOutboxRepository.complete(message);

        // Assert: Verify that the status is updated to "PUBLISHED"
        assertEquals("PUBLISHED", updatedOutbox.getStatus());
    }
}
