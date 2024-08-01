package com.hhdplus.concert_service.interfaces.controller;

import com.hhdplus.concert_service.application.dto.ChargeFacadeDto;
import com.hhdplus.concert_service.application.facade.ChargeFacade;
import com.hhdplus.concert_service.interfaces.dto.request.ChargeRequestDto;
import com.hhdplus.concert_service.interfaces.dto.response.ChargeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/charge")
@Tag(name = "충전", description = "충전 및 조회를 위한 API")
public class ChargeController {

    @Autowired
    private ChargeFacade chargeFacade;

    @GetMapping
    @Operation(summary = "잔액 조회")
    public ChargeResponseDto getUserAmount(@RequestBody ChargeRequestDto chargeRequestDto) {
        return ChargeResponseDto.toResponseDto(chargeFacade.getUserAmount(ChargeFacadeDto.toFacadeDto(chargeRequestDto)));
    }

    @PostMapping("/charge")
    @Operation(summary = "잔액 충전")
    public ChargeResponseDto chargeUserAmount(@RequestBody ChargeRequestDto chargeRequestDto) {
        return ChargeResponseDto.toResponseDto(chargeFacade.chargeUserAmount(ChargeFacadeDto.toFacadeDto(chargeRequestDto)));
    }

    @PostMapping("/use")
    @Operation(summary = "잔액 사용")
    public ChargeResponseDto useUserAmount(@RequestBody ChargeRequestDto chargeRequestDto) {
        return ChargeResponseDto.toResponseDto(chargeFacade.useUserAmount(ChargeFacadeDto.toFacadeDto(chargeRequestDto)));
    }
}
