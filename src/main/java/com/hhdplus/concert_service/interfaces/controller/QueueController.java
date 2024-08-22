package com.hhdplus.concert_service.interfaces.controller;

import com.hhdplus.concert_service.application.dto.QueueFacadeDto;
import com.hhdplus.concert_service.application.facade.QueueFacade;
import com.hhdplus.concert_service.application.facade.QueueRedisFacade;
import com.hhdplus.concert_service.interfaces.dto.request.QueueRequestDto;
import com.hhdplus.concert_service.interfaces.dto.response.QueueResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/token")
@Tag(name = "토큰", description = "토큰 생성 및 Queue 상태 조회 API")
public class QueueController {

    @Autowired
    QueueFacade queueFacade;

    @Autowired
    QueueRedisFacade queueRedisFacade;

    @PostMapping("/create")
    @Operation(summary = "토큰 생성")
    public QueueResponseDto createToken(@RequestBody QueueRequestDto dto) {
        return QueueResponseDto.toResponse(queueRedisFacade.createToken(QueueFacadeDto.toFacadeDto(dto)));
    }

    @GetMapping("/check")
    @Operation(summary = "Queue 순번 조회")
    public QueueResponseDto checkQueue(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("authorization");

        return QueueResponseDto.toResponse(queueRedisFacade.getQueueOrder(token));
    }
}
