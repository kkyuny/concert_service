## Step 19
1. 선정 부하 테스트
 - 선정 테스트: 토큰 발급 및 현재 나의 대기 번호 조회
 - 선정 사유: 특정한 인기콘서트 예매 시 토큰 발급 및 대기 번호 조회 요청이 많이 몰릴 것이라고 생각해보았습니다.
 - 테스트 시나리오: 10000명의 유저가 토큰 발급 후 대기 번호 조회를 각 5번씩 요청한다.
   ```
   export let options = {
      scenarios: {
          order_scenario: {
              vus: 10000, // 가상 사용자 수
              exec: 'queue_test',
              executor: 'per-vu-iterations', // 각각의 가상 사용자들이 정확한 반복 횟수만큼 실행
              iterations: 5 // 반복 횟수
          }
      }
   };

   export function queue_test() {    
       let userId = randomIntBetween(1, 50);
   
       // 토큰 발급 요청
       let token = create_token(userId);
       // 대기열 조회 요청
       check_queue_order(token);
   }
   
   function create_token(userId) {
       let createRequest =
           {
               userId: userId
           }
   
       // POST 요청 보내기
       let createToken = http.post(
           `http://localhost:8080/api/token/create`,
           JSON.stringify(createRequest),
           {   
               headers: {'Content-Type': 'application/json'},
               tags: {name: 'create token'}
           }
       )
   
       // 요청이 성공했는지 확인
       check(createToken, {'is status 200': (r) => r.status === 200});
   
       let jsonResponse = createToken.json();
       // console.log('API Response:', jsonResponse);
   
       return jsonResponse.token
   }
   
   function check_queue_order(token) {
       // GET 요청 보내기
       let checkResponse = http.get(
           `http://localhost:8080/api/token/check`,
           {
               headers: {
                   'authorization': token,  // 토큰을 헤더에 포함
               },
               tags: { name: 'check queue' }
           }
       );
   
       check(checkResponse, { 'is status 200': (r) => r.status === 200 });
   }
   ```
 - 테스트 결과(K6)
  
   ![image](https://github.com/user-attachments/assets/1846f52a-53ae-483a-b070-6d7732042321)

   -> 성공률 94.58%, 실행시간 1분 20초
 - 테스트 결과(그라파나)
   
  ![image](https://github.com/user-attachments/assets/d822fdaf-b9a6-46a0-b67c-dca077233313)


 1. 현재 내 대기순번 조회
  - 이유: 콘서트 예약 시 다수의 사용자가 대기 상태를 확인하기 위해서 수 많은 요청이 있을 수 있을 것이라고 생각<br>
  - 인덱싱 전 실행결과
   
    ![image](https://github.com/user-attachments/assets/a6cf5707-7b83-4094-8607-a56e18b1bf49)
  - 인덱싱 후 실행결과
   
    ![image](https://github.com/user-attachments/assets/9555f8da-298b-4f86-9eaf-6d801b250580)

  - 인덱싱 적용 테이블

    ![image](https://github.com/user-attachments/assets/9898fc4d-6d05-43ec-a0d4-8db0bf24292b)

    -> status만 인덱싱을 한 이유는 나머지 컬럼들의 카리널리티가 많아 큰 효율이 없을 것이라고 생각했다.
  
  - 실행 쿼리 및 테스트 코드
    ![image](https://github.com/user-attachments/assets/3a671a20-82df-40af-b9d4-c3a990316122)
```
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
```
  - 실행결과에 대한 고찰: 쿼리를 몇 번 실행했지만 성능이 개선되었다고 생각할 효과를 느끼지 못하였다.
    그 이유는 테스트 코드가 잘 못 되었거나 status의 도메인이 waiting, active 2가지 밖에 없고 또 한 토큰의 특성 상 status가 어느 정도는
    정렬이 되어있는 상태이기 때문에 인덱싱의 효과가 적은 것이 아닐까 생각했다.     
  
 2. 예약 가능한 콘서트 좌석 조회
  - 이유: 콘서트 예약이 오픈 되었을 때 예약 가능한 좌석을 확인하기 위해서 수 많은 요청이 있을 수 있을 것이라고 생각
  - 인덱싱 전 실행결과
    
    ![image](https://github.com/user-attachments/assets/678034b4-769a-426b-a449-89d14bff1599)

  - 인덱싱 후 실행결과
  
    ![image](https://github.com/user-attachments/assets/93c22c7d-7080-47cf-a5a0-d768d74c5732)

  - 인덱싱 적용 테이블

    ![image](https://github.com/user-attachments/assets/b3ac8985-1470-4cb0-a6ec-85d54dbbce94)

  - 실행 쿼리 및 테스트 코드

    ![image](https://github.com/user-attachments/assets/2e8202a4-3f24-4747-a12f-f02f0552e297)

    ```
    @Test
    @DisplayName("예약가능 좌석 테스트")
    void findReservedSeatsTest() throws ExecutionException, InterruptedException {
        Instant testStart = Instant.now();
        LOGGER.info("테스트 시작 시간 : {}", testStart);

        // Given: 콘서트 ID와 콘서트 날짜
        int numberOfUsers = 3000;
        Long concertId = 5L;
        LocalDateTime concertDate = LocalDateTime.of(2024, 8, 1, 19, 0);

        for (int i = 0; i < numberOfUsers; i++) {
            // When
            List<Long> result = concertService.findReservedSeats(concertId, concertDate);
            List<Long> expectedAvailableSeats = LongStream.rangeClosed(11, 50)
                    .boxed()
                    .collect(Collectors.toList());
            // Then
            assertThat(result).isNotNull();
            assertThat(result).containsExactlyInAnyOrderElementsOf(expectedAvailableSeats);
        }

        Instant testEnd = Instant.now();
        LOGGER.info("테스트 종료 시간 : {}", testEnd);
        LOGGER.info("테스트 총 경과 시간 : {} ms", Duration.between(testStart, testEnd).toMillis());
    }
    ```
    -> 예약 가능 좌석 조회 조건이 콘서트 id와 콘서트 date이기 때문에 두 컬럼을 복합키로하여 인덱싱을 생성해보았다.
     하지만 쿼리 성능이 개선이 되지 않았다. 이유는 2가지 정도를 생각해보았는데, 첫 번째는 역시 테스트 코드가 잘 못 되었을 것이다.
     콘서트를 여러개 생성하고 그 중 하나의 콘서트에 대해 수 많은 요청을 집어넣고 성능을 측정하려고 했던 것이 의도였다.
     코드를 동시에 실행시키지 않은 것도 문제일 수 있다고 생각하는데 그래도 쿼리 자체의 성능이 좋아지면 가시적인 효과가 보일 것이라고
     생각했는데 아니었다. 두 번째로는 예약가능한 좌석에 대한 카디널리티가 크지 않기 때문에 인덱싱의 효과가 미미한 것인가? 라는 생각을 해보았다.
    



   



