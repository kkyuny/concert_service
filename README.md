## Step 20
Step 19 수행 후 각 시나리오에서 공통적으로 시도 횟수를 크게 늘렸을 때 
(1)실패확률이 생기는 문제와 (2)처리하는 요청의 수가 급격히 낮아지면서 평균 처리속도가 올라가는 현상들을 발견하였다.

 1. 시도 횟수를 크게 늘렸을 때 실패확률이 생기는 문제
  - 문제점
   - 특정 사용자(약 120명)가 넘어갈 시 read: connection reset by peer 에러가 발생한다.
   ```
       queue_scenario: {
           vus: 120, // 가상 사용자
           exec: 'queue_test',
           executor: 'per-vu-iterations', // 각각의 가상 사용자들이 정확한 반복 횟수만큼 실행
           iterations: 10
       }
   ```     
     
  ![image](https://github.com/user-attachments/assets/53bfbe55-6f63-4a84-ae25-23375ad1982a)
     
   -> 사용자가 120명을 초과하는 테스트를 수행 시 해당 에러가 발생한다. 
   
  - 추정한 발생원인: read: connection reset by peer에는 여러가지 원인이 있지만 서버로의 요청이 제대로 이루어지지 않은 경우라고 
  생각해보았다. 

  - 해결방법: 문제 해결을 위해 톰캣의 설정을 아래와 같이 변경해보았다.
 
   ```
       server:
         tomcat:
           max-threads: 500
           max-connections: 10000
   ```
   
   톰캣의 설정을 이런식으로 변경하는 것은 적절하지 않고 로드 밸런싱 설정 등을 통한 것이 옳다고 생각한다.
   하지만 그것과 별개로 톰캣의 설정을 변경해보았지만 여전히 Request Faield가 발생하였다.
  
   ```
     queue_scenario: {
         vus: 100, // 가상 사용자
         exec: 'queue_test',
         executor: 'per-vu-iterations', // 각각의 가상 사용자들이 정확한 반복 횟수만큼 실행
         iterations: 100
     }
   ```
   
   와 같이 사용자는 줄이고 시도횟수를 늘렸을 때 해당 에러가 발생하지 않는 점으로 보았을 때 생각한 원인이 맞을 것 같다는 생각은 해보았지만
   로드밸런싱을 구축할 환경이 되지 못하여 테스트를 못해본 점이 아쉽다.

 2. 처리하는 요청의 수가 급격히 낮아지면서 평균 처리속도가 올라가는 현상
  - 문제점
   1) 특정 사용자(약 10000명)가 넘어갈 시 처리하는 요청의 수가 급격히 낮아진다.
    - 5000명이 5번씩 수행 시 결과
     
      ![image](https://github.com/user-attachments/assets/4813c433-9d8d-41e6-9ceb-0b066a29e902)

      ![image](https://github.com/user-attachments/assets/43f760df-3c8c-418a-9877-32fccb23e7ff)

      -> 요청의 처리 수가 일정하며, 전체 수행시간은 7초가 걸리고 성공률로 98%로 높은 편이라고 생각한다.

   - 10000명이 5번씩 수행 시 결과

      ![image](https://github.com/user-attachments/assets/6b5cac1a-59ca-4f5a-b008-da3fa638bf46)

      ![image](https://github.com/user-attachments/assets/4e341756-153f-4203-914b-338745da56b8)

      -> 요청의 처리가 일정 수를 넘어갈 시 요청의 처리 수가 급격하게 낮아지며, 전체 수행시간은 1분20초, 성공률은 93%의 결과가 나왔다.
  
   - 추정한 발생원인: 앞서 결과를 보았을 때 유저수는 2배 증가하였지만 처리 속도는 10배 이상 증가하였다.
      일정 처리 수가 넘어갈 시 처리하는 요청의 수가 낮아지는 점을 보았을 때 서버의 성능 상의 이슈가 발생하였다고 추정해보았다.

