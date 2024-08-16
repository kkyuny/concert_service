package com.hhdplus.concert_service.infrastructure.implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhdplus.concert_service.business.message.PaymentMessage;
import com.hhdplus.concert_service.business.message.PaymentMessageOutboxWriter;
import com.hhdplus.concert_service.infrastructure.entity.PaymentOutbox;
import com.hhdplus.concert_service.infrastructure.repository.PaymentOutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentOutboxRepositoryImpl implements PaymentMessageOutboxWriter {

    @Autowired
    private ObjectMapper objectMapper;

    private final PaymentOutboxJpaRepository jpaRepository;

    // PaymentEventListener -> createOutboxMessage(PaymentEvent event) 실행.
    @Override
    public PaymentOutbox save(PaymentMessage message) throws JsonProcessingException {
        PaymentOutbox entity = new PaymentOutbox();
        entity.setId(message.getId());
        entity.setMessage(objectMapper.writeValueAsString(message));
        entity.setStatus("INIT");
        entity.setCreateDate(LocalDateTime.now());

        return jpaRepository.save(entity);
    }

    // PaymentMessageConsumer -> complete(String message) 실행.
    @Override
    public PaymentOutbox complete(PaymentMessage message) {
        PaymentOutbox entity = jpaRepository.findById(message.getId()).orElseThrow();
        entity.setId(message.getId());
        entity.setStatus("PUBLISHED");
        entity.setUpdateDate(LocalDateTime.now());

        return jpaRepository.save(entity);
    }

    @Override
    public List<PaymentOutbox> findByStatus(String init) {
        return jpaRepository.findByStatus(init);
    }
}
