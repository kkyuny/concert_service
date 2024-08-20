package com.hhdplus.concert_service.integrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.tempSender.ExternalSender;
import com.hhdplus.concert_service.infrastructure.entity.PaymentOutbox;
import com.hhdplus.concert_service.infrastructure.implement.PaymentOutboxRepositoryImpl;
import com.hhdplus.concert_service.infrastructure.kafka.PaymentKafkaMessageSender;
import com.hhdplus.concert_service.infrastructure.repository.PaymentOutboxJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"Payment"})
class PaymentEventIntegrationTest {

    @Autowired
    private PaymentKafkaMessageSender paymentKafkaMessageSender;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PaymentOutboxJpaRepository paymentOutboxJpaRepository;

    @Autowired
    private PaymentOutboxRepositoryImpl paymentOutboxRepository;

    @MockBean
    private ExternalSender externalSender; // PaymentMessageConsumer가 사용하는 외부 발송 모듈을 Mock

    private PaymentMessage paymentMessage;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        // 테스트에 사용할 PaymentMessage 객체를 초기화
        paymentMessage = PaymentMessage.builder()
                .userId(1L)
                .paymentId(100L)
                .status("INIT")
                .build();

        PaymentOutbox savedOutbox = paymentOutboxRepository.save(paymentMessage);
        paymentMessage.setId(savedOutbox.getId());
    }

    @Test
    @DisplayName("Kafka로 메시지 전송 및 수신 테스트")
    void testSendAndReceiveMessage() throws JsonProcessingException, InterruptedException, ExecutionException {

        // Kafka로 메시지 전송(컨슈머에서 outbox 상태 변경과 외부로 메세지 send)
        paymentKafkaMessageSender.send(paymentMessage);

        // KafkaListener가 메시지를 수신했는지 확인
        ArgumentCaptor<PaymentMessage> captor = ArgumentCaptor.forClass(PaymentMessage.class);
        verify(externalSender, Mockito.timeout(5000)).sendPaymentResult(captor.capture());

        PaymentMessage receivedMessage = captor.getValue();

        // 수신된 메시지가 전송된 메시지와 동일한지 확인
        assertThat(receivedMessage).isNotNull();
        assertThat(receivedMessage.getUserId()).isEqualTo(paymentMessage.getUserId());
        assertThat(receivedMessage.getPaymentId()).isEqualTo(paymentMessage.getPaymentId());
        assertThat(receivedMessage.getStatus()).isEqualTo(paymentMessage.getStatus());

        // Outbox의 상태가 "PUBLISHED"로 변경되었는지 확인
        PaymentOutbox paymentOutbox = paymentOutboxRepository.findByPaymentId(paymentMessage.getPaymentId()).get();
        assertThat(paymentOutbox.getStatus()).isEqualTo(paymentOutbox.getStatus());
    }
}