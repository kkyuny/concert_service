Step 15
1. 현재 시나리오에서 인덱스가 필요하다고 판단한 쿼리
 1) 현재 내 대기순번 조회
  - 이유: 콘서트 예약 시 다수의 사용자가 대기 상태를 확인하기 위해서 수 많은 요청을 할 수 있을 것이라고 생각<br>
  - 인덱싱 전 실행결과
   
    ![image](https://github.com/user-attachments/assets/a6cf5707-7b83-4094-8607-a56e18b1bf49)
  - 인덱싱 후 실행결과
   
    ![image](https://github.com/user-attachments/assets/9555f8da-298b-4f86-9eaf-6d801b250580)

  - 인덱싱 적용 테이블

    ![image](https://github.com/user-attachments/assets/9898fc4d-6d05-43ec-a0d4-8db0bf24292b)
    -> status만 인덱싱을 한 이유는 나머지 컬럼들의 카리널리티가 많아 큰 효율이 없을 것이라고 생각했다.
  
  - 실행 쿼리 및 테스트 코드
    ![image](https://github.com/user-attachments/assets/3a671a20-82df-40af-b9d4-c3a990316122)

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

  - 실행결과에 대한 고찰: 쿼리를 몇 번 실행했지만 성능이 개선되었다고 생각할 효과를 느끼지 못하였다.
    그 이유는 테스트 코드가 잘 못 되었거나 status의 도메인이 waiting, active 2가지 밖에 없고 또 한 토큰의 특성 상 status가 어느 정도는
    정렬이 되어있는 상태이기 때문에 인덱싱의 효과가 적은 것이 아닐까 생각했다.
      
     
    

  

  
 2) 예약 가능한 콘서트 좌석 조회 
