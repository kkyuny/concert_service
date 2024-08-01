package com.hhdplus.concert_service.integrationTest;

import com.hhdplus.concert_service.application.dto.ChargeFacadeDto;
import com.hhdplus.concert_service.application.facade.ChargeFacade;
import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ChargeFacadeIntegrationTest {

    @Autowired
    private ChargeFacade chargeFacade;

    @Autowired
    private UserRepository userRepository;

    private UserDomain testUser;

    @BeforeEach
    void setUp() {
        testUser = UserDomain.builder().userId(1L).amount(1000L).build();
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("유저 금액 조회 테스트")
    void getUserAmountTest() {
        ChargeFacadeDto dto = ChargeFacadeDto.builder().userId(testUser.getUserId()).build();
        ChargeFacadeDto result = chargeFacade.getUserAmount(dto);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("유저 금액 충전 테스트")
    void chargeUserAmountTest() {
        ChargeFacadeDto dto = ChargeFacadeDto.builder().userId(testUser.getUserId()).amount(500L).build();
        ChargeFacadeDto result = chargeFacade.chargeUserAmount(dto);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(1500L);
    }

    @Test
    @DisplayName("유저 금액 사용 테스트")
    void useUserAmountTest() {
        ChargeFacadeDto dto = ChargeFacadeDto.builder().userId(testUser.getUserId()).amount(200L).build();
        ChargeFacadeDto result = chargeFacade.useUserAmount(dto);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualTo(800L);
    }
}