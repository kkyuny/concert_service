## Step 19
부하 테스트 시나리오는 토큰 발급 및 현재 나의 대기 번호 조회와 특정 콘서트의 예약가능한 좌석 조회 2가지의 시나리오로 테스트를 진행해보았습니다.

1. 토큰 발급 및 현재 나의 대기 번호 조회
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

 - 테스트 결과(그라파나)
   
   ![image](https://github.com/user-attachments/assets/d822fdaf-b9a6-46a0-b67c-dca077233313)

   -> 2번의 테스트를 진행하였으며 테스트 실행결과 성공률 약 95%, 실행시간은 약 1분 20~30초가 소요되었다.
   
   -> 특이한 점은 그라파나의 Virtual Users, Requests per Second를 보면 요청이 어느정도 수행되다가 처리하는 성능이 급격히 낮아졌다.

 
 2. 특정 콘서트의 예약가능한 좌석 조회 
  - 선정 사유: 특정한 인기콘서트 예매 시 예약가능한 좌석조회 요청을 많이 시도할 것이라고 생각해보았습니다.
  - 테스트 시나리오: 10000명의 유저가 특정한 콘서트에 대해 예약가능한 좌석조회 요청을 각 5번씩 요청한다.
    ```
    export let options = {
        scenarios: {
            order_scenario: {
                vus: 10000, // 가상 사용자
                exec: 'concert_test',
                executor: 'per-vu-iterations', // 각각의 가상 사용자들이 정확한 반복 횟수만큼 실행
                iterations: 5
            }
        }
    };
    
    export function concert_test() {   
        find_availabled_seats()
    }
    
    function find_availabled_seats() {
        // GET 요청 보내기
        let findSeatsResponse = http.get(
            `http://localhost:8080/api/concert/findSeats?concertId=1&concertDate=2024-09-15T20:00:00`,
            {
                headers: {
                    'authorization': "testToken",
                },
                tags: { name: 'find seats' }
                }
            );
        check(findSeatsResponse, { 'is status 200': (r) => r.status === 200 });
        }
    }
    ```
  - 테스트 결과(K6)
   
   ![image](https://github.com/user-attachments/assets/b921b794-60d6-455c-b37b-e2daca261b49)



   



