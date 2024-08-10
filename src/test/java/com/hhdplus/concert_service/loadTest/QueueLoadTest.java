package com.hhdplus.concert_service.loadTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hhdplus.concert_service.business.domain.QueueDomain;
import com.hhdplus.concert_service.business.repository.QueueRepository;
import com.hhdplus.concert_service.business.service.QueueService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
public class QueueLoadTest {

    static Logger LOGGER = LoggerFactory.getLogger(QueueLoadTest.class);

    @Mock
    QueueRepository queueRepository;

    @InjectMocks
    QueueService queueService;

    private static final int NUMBER_OF_USERS = 10000;
    private static final int MAX_ACTIVE_COUNT = 100;
    private List<QueueDomain> queues;

    @BeforeEach
    void setUp() {
        final int[] activeCount = {0};

        LOGGER.info("setUp 메서드 시작");

        queues = IntStream.range(0, NUMBER_OF_USERS)
                .mapToObj(i -> {
                    String status;
                    if (activeCount[0] < MAX_ACTIVE_COUNT) {
                        status = "active";
                        activeCount[0]++;
                    } else {
                        status = "waiting";
                    }
                    LocalDateTime now = LocalDateTime.now().minusSeconds(NUMBER_OF_USERS - i);
                    return QueueDomain.builder()
                            .token(UUID.randomUUID().toString())
                            .userId((long) i)
                            .status(status)
                            .validDate(now.plusMinutes(5))
                            .regiDate(now)
                            .build();
                })
                .collect(Collectors.toList());
        queueRepository.saveAll(queues);

        LOGGER.info("setUp 메서드 종료");
    }

    @Test
    @DisplayName("대기열 순서 조회 테스트")
    void getQueueOrderConcurrencyTest() throws InterruptedException, ExecutionException {

        Instant testStart = Instant.now();
        LOGGER.info("테스트 시작 시간 : {}", testStart);

        Random random = new Random();

        for (int i = 0; i < NUMBER_OF_USERS; i++) {
            // Given
            List<QueueDomain> waitingQueues = queues.stream()
                    .filter(q -> "waiting".equals(q.getStatus()))
                    .toList();

            QueueDomain queue = waitingQueues.get(random.nextInt(waitingQueues.size()));

            when(queueRepository.findWaitingQueuesBeforeMe(queue.getToken()))
                    .thenReturn(queues.stream()
                            .filter(q -> "waiting".equals(q.getStatus()) && q.getRegiDate().isBefore(queue.getRegiDate()))
                            .toList());

            // When
            QueueDomain result = queueService.getWaitingUserCountBeforeMe(queue);

            // Then
            long expectedWaitingCount = queues.stream()
                    .filter(q -> "waiting".equals(q.getStatus()) && q.getRegiDate().isBefore(queue.getRegiDate()))
                    .count();

            assertThat(result).isNotNull();
            assertThat(result.getQueueCount()).isEqualTo(expectedWaitingCount);
        }

        Instant testEnd = Instant.now();
        LOGGER.info("테스트 종료 시간 : {}", testEnd);
        LOGGER.info("테스트 총 경과 시간 : {} ms", Duration.between(testStart, testEnd).toMillis());
    }
}
