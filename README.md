# MySQL vs MongoDB 채팅 시스템에 적합한 DBMS 선정 실험


## 1. 서론
채팅 시스템은 실시간 데이터 처리와 안정성이 중요한 현대 애플리케이션의 핵심 요소다. 데이터 저장과 처리 성능은 채팅 시스템의 성공적인 구현에 필수적이며, 데이터베이스의 선택은 이를 결정짓는 중요한 요인 중 하나이다. 본 실험에서는 MongoDB와 MySQL을 대상으로, 채팅 시스템의 요구사항을 충족시키는 데이터베이스의 적합성을 분석한다. 이에 따라 적합하다 판단 되는 DBMS를 실제 운영중인 서비스에 도입하며 성능측정을 통해 효용성을 확인한다.



---

## 2. 실험 배경

### 2.1 선정 주제
Friendship 애플리케이션의 채팅 서비스를 구현하는 과정에서, 채팅 데이터를 효과적으로 관리하는 방안에 대한 논의가 이번 실험의 출발점이 되었다. 채팅 서비스는 실시간성을 보장해야 할 뿐만 아니라, 채팅방을 기준으로 복잡한 집계 쿼리를 처리할 수 있는 데이터베이스가 필요했다. 또한, 대량의 채팅 메시지가 저장될 것으로 예상됨에 따라, 데이터 저장과 처리를 효율적으로 하기 위해 샤딩 기법을 포함한 다양한 접근 방식을 검토하게 되었다.


### 2.2 채팅 서버 요구사항
1. **채팅 메시지 저장**:채팅 메시지를 주고받을 때, 이를 서버에 	저장하여 기록으로 남겨야 한다.
   
2. **최근 채팅 메시지 100개 조회**: 애플리케이션은 로컬에 저장된 데이터와 서버에 	저장된 데이터를 비교하여 정합성을 유지해야 	한다. 이를 위해 최근 채팅 메시지 100개를 	조회하여 로컬 데이터와 일치하도록 동기화한다.
   
3. **메시지 읽음 처리**: Friendship 애플리케이션의 채팅 시스템에서는 	상대방이 메시지의 읽음 여부를 확인할 수 	있어야 한다. 이를 위해 메시지 읽음 상태를 	서버에 기록하는 읽음 처리 요청이 필요하다. 

4. **읽지 않은 메시지 정보 제공**: 사용자가 채팅 가능한 상대를 조회할 때, 각 	채팅방에서 읽지 않은 메시지의 개수와 가장 	최근 메시지를 함께 제공한다. 이를 통해 	사용자는 채팅방에 들어가기 전에 필요한 정보를 	손쉽게 확인할 수 있다. 


---


## 3. 본론


### 3.1 사용 시나리오
이 애플리케이션에서는 하루에 약 100쌍의 사용자가 매칭된다고 가정한다. 이를 통해 하루 동안 100개의 채팅방이 생성되며, 각 채팅방에서 평균 100개의 메시지가 오간다. 결과적으로 하루 기준 약 10,000개의 메시지가 저장된다. 또한, 채팅 메시지는 사용자 보호를 위해 고소와 같은 상황을 대비하여 1년간 보관하며, 필요에 따라 저장 기간이 변경될 가능성도 고려된다. 

### 3.2 데이터 규모
- **하루 저장 메시지**: 약 10,000건
- **1년 누적 메시지**: 약 3,650,000건
- **누적 채팅방 수**: 약 36,500개
- 각 채팅방 평균 메시지 수: 읽은 메시지 80개, 읽지 않은 메시지 20개

### 3.3 성능 측정
애플리케이션 수준에서 시스템 시간을 측정하여 각 요청에 대한 응답 시간을 평가한다. 특히, 2.2.3에 해당하는 읽지 않은 메시지 정보 제공 기능은 복잡한 쿼리와 샤딩된 데이터베이스 접근을 포함하므로, JMeter를 활용하여 부하 테스트를 수행한다. 이를 통해 성능을 분석하고, 시스템이 높은 부하 환경에서도 안정적으로 작동할 수 있는지 검증한다. 


### 3.4 실험 환경 설정
1. **MongoDB**:
   - PSA 아키텍처 기반으로 Config 서버 2대, Shard 서버 2대, Mongos 서버 1대 구성.
  
     
2. **MySQL**:
   - 애플리케이션 레벨에서 샤딩 구현.
   - MySQL 서버 2대 구성.

3. **공통 조건**:
   - 인덱스 적용 및 `sort`, `limit` 활용.
   - 각 샤드에 채팅방 ID 기준 18,000개의 데이터 저장.
   - 채팅방 ID를 기준으로 Range Sharding 적용.

---

## 4. 실험 결과

### 4.1 애플리케이션 레벨의 응답시간 측정

| 작업                         | MongoDB 응답 시간 | MySQL 응답 시간 |
|-----------------------------|-----------------|----------------|
| 메시지 저장                  | 89ms            | 80ms           |
| 메시지 읽음 처리             | 79ms            | 80ms           |
| 특정 채팅방 메시지 조회       | 74ms            | 120ms          |
| 읽지 않은 메시지 정보 제공    | 92ms            | 2002ms         |

### 4.2 부하 테스트 (JMeter)
- **테스트 조건**:
  - 100명의 사용자가 동시에 요청.
  - Ramp-up 시간: 1초.
  - 요청 비율:
    - 저장 요청: 60%
    - 읽기 요청: 36%
    - 채팅방 정보 조회: 2%
    - 최근 메시지 100개 조회: 2%

| 지표                         | MongoDB          | MySQL           |
|-----------------------------|------------------|-----------------|
| 평균 응답 시간               | 21ms            | 25ms           |
| 최대 응답 시간               | 67ms            | 260ms          |
| 표준 편차                   | 8.9ms           | 10.51ms        |
| 처리량 (Throughput)          | 1617.6 요청/초  | 1541.8 요청/초 |

테스트 결과, MongoDB는 평균 응답 시간, 최대 응답 시간, 표준 편차 측면에서 MySQL보다 우수한 성능을 보였다. 특히, 최대 응답 시간이 MySQL에 비해 크게 낮았으며, 처리량도 더 높았다. 이는 MongoDB가 동시 요청 처리와 대규모 데이터 접근에서 MySQL보다 효율적임을 나타낸다. 


---

## 5. 결과 분석

### 단순 쿼리에 대한 결과 분석
1. **샤딩 환경과의 연관성**  
   MongoDB와 MySQL 모두 데이터가 샤딩된 상태에서 실험이 진행되었으나, 단순 쿼리의 경우 단일 데이터베이스에만 접근하므로 샤딩이 성능에 미치는 영향은 미미한 것으로 나타났습니다.

2. **DBMS의 연산 속도**  
   - **MongoDB**: 요청 단위로 auto-commit을 처리하여 단순 쿼리에서 빠른 성능을 보였습니다. 또한, 데이터를 BSON 형식으로 압축 저장하기 때문에 데이터 접근 및 처리 속도가 더 빠른 것으로 확인되었습니다.
   - **MySQL**: 관계형 데이터베이스 특성상 데이터 정합성을 보장하기 위해 추가적인 검증 단계를 수행하며, 이로 인해 시간이 더 소요되었습니다.

### 집계 연산에 대한 결과 분석
1. **샤딩 환경과의 연관성**  
   집계 연산은 `roomId`를 기준으로 Range Sharding된 데이터베이스에서 서로 다른 샤드에 접근하도록 설계되었습니다. 성능 차이는 미미했지만, 관리 편의성 면에서 차이가 있었습니다. MySQL은 샤드를 추가할 때 애플리케이션 개발자의 작업이 필요했지만, MongoDB는 자동화된 과정을 제공하여 관리가 더 용이했습니다.

2. **DBMS의 연산 속도**  
   - **MongoDB**: Aggregation Framework를 활용해 파이프라인 기반으로 메모리 내에서 연산을 처리하며, 서브쿼리 없이 데이터를 효율적으로 필터링해 빠른 결과를 도출했습니다.
   - **MySQL**: 집계 연산 시 서브쿼리를 사용하는 구조로 인해 임시 테이블 생성 및 추가적인 I/O 작업이 발생했으며, 데이터 재스캔 과정에서 처리 속도가 저하되었습니다.

---

## 6. 결론
- **MongoDB**:
  - **장점**: 유연한 데이터 구조, 뛰어난 확장성, Aggregation Framework 활용으로 복잡한 연산에서 높은 성능 제공.
  - **적합성**: 실시간성과 확장성이 필요한 채팅 시스템에 적합.

- **MySQL**:
  - **장점**: 데이터 정합성과 일관성 보장.
  - **적합성**: 데이터 정합성이 중요한 시스템에 적합.

본 실험은 채팅 시스템에서 MongoDB와 MySQL의 성능, 확장성, 데이터 저장 방식을 비교하여 적합성을 분석했습니다. 연구 결과, **MongoDB는 집계 연산과 복잡한 쿼리 처리에서 높은 성능**을 보이며, NoSQL 데이터베이스의 유연성과 확장성 덕분에 실시간 채팅 데이터 관리에 최적임을 확인했습니다. 반면, **MySQL은 데이터 정합성과 일관성 측면에서 강점**을 보였으며, 단순 쿼리의 성능도 MongoDB와 유사했습니다.

MongoDB는 BSON 기반 저장 방식과 Aggregation Framework를 활용하여 메모리 효율성과 처리 속도에서 MySQL을 능가했습니다. 반대로, MySQL은 임시 테이블 생성 및 서브쿼리로 인해 I/O 작업이 추가되며 상대적으로 느린 성능을 보였습니다.  
따라서, **MongoDB는 확장성과 복잡한 데이터 처리가 필요한 시스템에 적합한 선택**임을 입증했습니다.
