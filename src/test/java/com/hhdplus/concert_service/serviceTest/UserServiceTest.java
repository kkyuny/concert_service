package com.hhdplus.concert_service.serviceTest;

import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.business.service.UserService;
import com.hhdplus.concert_service.infrastructure.entity.User;
import com.hhdplus.concert_service.interfaces.common.exception.InvalidReqBodyException;
import com.hhdplus.concert_service.interfaces.common.exception.RequestTimeoutException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @DisplayName("유저를 ID로 조회하는 테스트")
    @Test
    void findUserByIdTest() {
        //given
        long userId = 1L;
        long amount = 100L;
        UserDomain user = UserDomain.builder().userId(userId).amount(amount).build();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));

        //then
        assertThat(userService.findUserById(userId)).isEqualTo(user);
    }

    @DisplayName("유저를 ID로 조회하는 실패 테스트")
    @Test
    void findNotExistUserByIdTest() {
        //given
        long userId = 1L;
        long amount = 100L;

        //when & then
        assertThatThrownBy(() -> userService.findUserById(userId))
                .isInstanceOf(InvalidReqBodyException.class);
    }

    @DisplayName("잔액 충전 테스트")
    @Test
    void chargePointUserTest() {
        //given
        long userId = 1L;
        long amount = 100L;
        long chargeAmount = 100L;
        UserDomain user = UserDomain.builder().userId(userId).amount(amount).build();

        //when
        when(userRepository.save(any(UserDomain.class))).thenReturn(user);
        UserDomain result = userService.chargeAmountUser(user, chargeAmount);

        //then
        assertThat(result.getAmount()).isEqualTo(200L);
    }

    @DisplayName("잔액 충전 실패 테스트(충전 값이 0 이하)")
    @Test
    void chargePointUserFailTest() {
        //given
        long userId = 1L;
        long amount = 100L;
        long chargeAmount = -100L;
        UserDomain user = UserDomain.builder().userId(userId).amount(amount).build();

        //when & then
        assertThatThrownBy(() -> userService.chargeAmountUser(user, chargeAmount))
                .isInstanceOf(InvalidReqBodyException.class);
    }

    @DisplayName("잔액 사용 테스트")
    @Test
    void usePointUserTest() {
        //given
        long userId = 1L;
        long amount = 300L;
        long useAmount = 100L;
        UserDomain user = UserDomain.builder().userId(userId).amount(amount).build();

        //when
        when(userRepository.save(any(UserDomain.class))).thenReturn(user);
        UserDomain result = userService.useAmountUser(user, useAmount);

        //then
        assertThat(result.getAmount()).isEqualTo(200L);
    }

    @DisplayName("잔액 사용 실패 테스트(충전 값이 0 이하)")
    @Test
    void useInvalidPointUserFailTest() {
        //given
        long userId = 1L;
        long amount = 300L;
        long useAmount = -100L;
        UserDomain user = UserDomain.builder().userId(userId).amount(amount).build();

        //when & then
        assertThatThrownBy(() -> userService.useAmountUser(user, useAmount))
                .isInstanceOf(InvalidReqBodyException.class);
    }

    @DisplayName("잔액 사용 실패 테스트(잔액 < 사용)")
    @Test
    void useOverPointUserFailTest() {
        //given
        long userId = 1L;
        long amount = 300L;
        long useAmount = 500L;
        UserDomain user = UserDomain.builder().userId(userId).amount(amount).build();

        //when & then
        assertThatThrownBy(() -> userService.useAmountUser(user, useAmount))
                .isInstanceOf(InvalidReqBodyException.class);
    }
}
