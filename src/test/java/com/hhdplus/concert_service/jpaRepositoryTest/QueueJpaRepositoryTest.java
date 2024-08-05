package com.hhdplus.concert_service.jpaRepositoryTest;

import com.hhdplus.concert_service.business.domain.UserDomain;
import com.hhdplus.concert_service.business.repository.UserRepository;
import com.hhdplus.concert_service.infrastructure.entity.Queue;
import com.hhdplus.concert_service.infrastructure.repository.QueueJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class QueueJpaRepositoryTest {

    @Autowired
    private QueueJpaRepository queueJpaRepository;

    @Autowired
    private UserRepository userRepository;

    private UserDomain testUser;
    private Queue testQueue;

    @BeforeEach
    void setUp() {
        // 유저 엔티티 생성 및 저장
        testUser = UserDomain.builder().userId(0L).amount(1000L).build();
        testUser = userRepository.save(testUser);

        // 토큰 생성 및 저장
        testQueue = Queue.builder()
                .token("test-token")
                .userId(testUser.getUserId())
                .status("active")
                .validDate(LocalDateTime.now().plusMinutes(5))
                .regiDate(LocalDateTime.now())
                .build();
        testQueue = queueJpaRepository.save(testQueue);
    }

    @Test
    void testFindByUserId() {
        Optional<Queue> queueOpt = queueJpaRepository.findByUserId(testUser.getUserId());
        assertThat(queueOpt).isPresent();
        Queue queue = queueOpt.get();
        assertThat(queue.getToken()).isEqualTo("test-token");
        System.out.println("Found Queue: " + queue);
    }
}