package com.hhdplus.concert_service.serviceTest;

import com.hhdplus.concert_service.business.domain.PaymentDomain;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.PaymentRepository;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.business.service.PaymentService;
import com.hhdplus.concert_service.business.service.UserService;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @DisplayName("결제 테스트")
    @Test
    public void testSavePayment() {
        // given
        PaymentDomain paymentDomain = PaymentDomain.builder()
                .userId(1L)
                .amount(1000L)
                .build();

        PaymentDomain savedPaymentDomain = PaymentDomain.builder()
                .userId(1L)
                .amount(1000L)
                .build();

        when(paymentRepository.save(any(PaymentDomain.class))).thenReturn(savedPaymentDomain);

        // when
        PaymentDomain result = paymentService.savePayment(paymentDomain);

        // then
        verify(paymentRepository, times(1)).save(paymentDomain);
        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(savedPaymentDomain.getAmount());
    }

}
