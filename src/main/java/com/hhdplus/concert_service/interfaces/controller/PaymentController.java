package com.hhdplus.concert_service.interfaces.controller;

import com.hhdplus.concert_service.application.dto.PaymentFacadeDto;
import com.hhdplus.concert_service.application.facade.PaymentFacade;
import com.hhdplus.concert_service.interfaces.dto.request.PaymentRequestDto;
import com.hhdplus.concert_service.interfaces.dto.response.PaymentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payment")
@Tag(name = "결제", description = "콘서트 결제 API")
public class PaymentController {

    @Autowired
    PaymentFacade paymentFacade;

    @PostMapping
    @Operation(summary = "콘서트 예약 내역 결제")
    public PaymentResponseDto executePayment(HttpServletRequest request, @RequestBody PaymentRequestDto dto) {
        String token = request.getHeader("authorization");

        return PaymentResponseDto.toResponse(paymentFacade.executePayment(token, PaymentFacadeDto.toFacadeDto(dto)));
    }
}
