# K사 서버 개발 과제

다수 서버 환경에서도 안정적으로 동작하는 커피숍 주문 시스템입니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Database | MySQL |
| Cache | Redis |
| Message Queue | Apache Kafka 3.7.0 |
| ORM | Spring Data JPA, QueryDSL |
| 결제 | PortOne V2 |
| 부하 테스트 | K6 |
| 인프라 | Docker, Docker Compose |

---

## ERD

<img width="1069" height="486" alt="image" src="https://github.com/user-attachments/assets/8be75dad-4b78-45e2-8f46-e49e47abe5e6" />


---

## API 명세

### 메뉴

| Method | URI | 설명 |
|--------|-----|------|
| POST | /api/menus | 메뉴 등록 |
| GET | /api/menus | 메뉴 목록 조회 |
| GET | /api/menus/popular | 인기 메뉴 TOP 3 조회 |

### 결제

| Method | URI | 설명 |
|--------|-----|------|
| POST | /api/payments/charge/{userId} | PortOne 결제 검증 후 포인트 충전 |

### 주문

| Method | URI | 설명 |
|--------|-----|------|
| POST | /api/orders/{userId} | 커피 주문 및 결제 |

---

## 설계 의도 및 문제 해결 전략

### 1. 동시성 제어

포인트 차감 시 다수 서버에서 동시 요청이 들어오면 데이터 불일치가 발생할 수 있습니다.
비관적 락을 적용해 한 번에 하나의 트랜잭션만 처리하도록 했습니다.

포인트는 금액 관련 데이터로 정확도가 최우선입니다.
낙관적 락은 충돌 시 재시도가 필요한데, 포인트처럼 충돌 가능성이 높은 경우에는 비관적 락이 더 안전하다고 판단했습니다.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select u from User u where u.id = :userId")
Optional<User> findByIdWithLock(@Param("userId") Long userId);
```

테스트 결과: 포인트 5000P 보유 유저가 3000P짜리 커피를 동시에 2번 주문했을 때 1건만 성공하고 남은 포인트는 2000P로 정확하게 처리되었습니다.

---

### 2. 멱등성 보장

같은 결제 요청이 중복으로 처리되는 것을 방지하기 위해 애플리케이션 레벨과 DB 레벨에서 이중으로 방어했습니다.

- existsByImpUid() 체크 (애플리케이션 레벨)
- imp_uid UNIQUE 제약 (DB 레벨)

테스트 결과: 같은 paymentId로 5번 동시 요청 시 1건만 성공하고 나머지 4건은 중복으로 처리되지 않았습니다.

---

### 3. 데이터 수집 플랫폼 전송 - Kafka

주문 완료 시 데이터 수집 플랫폼으로 실시간 전송이 필요합니다.
트래픽 폭증 때 서버 과부하 위험이 있어 Kafka를 통한 비동기 이벤트 처리 방식을 선택했습니다.

주문 API 호출 시 Kafka에 이벤트를 발행하고 바로 응답합니다.
Consumer가 비동기로 Redis ZSET 점수 업데이트와 DB orderCount 증가를 처리합니다.

3개 브로커 클러스터 환경을 구성했습니다.
실제 운영 환경에서는 Replication Factor를 3으로 설정해 고가용성을 확보할 수 있습니다.
(실제 사용은 1개만 하고있습니다.)

---

### 4. 인기 메뉴 조회 - Redis ZSET

최근 7일 인기 메뉴를 빠르게 조회하기 위해 날짜별 Redis ZSET으로 관리합니다.

주문 발생 시 menu:ranking:{날짜} 키에 menuId 점수를 1 증가시킵니다.
조회 시 7일치 ZSET을 합산해 TOP 3를 반환합니다.

Redis 데이터가 유실될 경우를 대비해 Menu 테이블에 order_count 컬럼을 추가해 DB에도 집계 데이터를 저장합니다.
날짜별 키는 8일 후 TTL로 자동 삭제됩니다.

---

### 5. 메뉴 목록 조회 - QueryDSL + Redis Cache

메뉴 목록 조회는 빈번하지만 데이터 변경은 적어 Redis Cache를 적용했습니다.
키워드 검색은 QueryDSL 동적 쿼리로 처리합니다.

메뉴 등록 시 @CacheEvict로 캐시를 초기화해 항상 최신 데이터를 반환합니다.
TTL은 10분으로 설정했습니다.

---

## 테스트

### 단위 테스트
- PaymentServiceTest: 결제 검증, 멱등성, 금액 불일치, 결제 상태 검증
- OrderServiceTest: 정상 주문, 메뉴/유저 없음, 포인트 부족

### 동시성 테스트
- UserServiceTest: 다수 서버 동시 충전 요청 시 포인트 정확도 검증
- UserServiceTest: 동시 중복 결제 요청 시 1건만 처리 검증
- OrderConcurrencyTest: 포인트 부족 상태에서 동시 주문 시 1건만 성공 검증

---

## 실행 방법

```bash

# 프로젝트 클론
git clone https://github.com/S1K1DA/k-server-project.git
cd k_server_project

# 환경 변수 설정
application.yml에 값을 설정했습니다.
- MySQL 접속 정보
- 포트원 API Secret, Store ID, Channel Kwy

# Redis 실행
docker run -d --name redis -p 6379:6379 redis

# Kafka 클러스터 실행
docker-compose up -d

# 서버 실행
./gradlew bootRun

```

Kafka UI: http://localhost:8088

---

## 성능 테스트 (K6)

로컬 환경에서 진행한 테스트로 실제 운영 환경과 수치 차이가 있을 수 있습니다.
부하 테스트 도구 사용법과 성능 측정 방법 학습을 목적으로 진행했습니다.
과제 목적 및 테스트 관련 고민을 한 결과
커피를 주문하는 시스템을 구현하는것이므로 몇십만 더미데이터를 넣어서 테스트한것이아닌
간단하게 13개의 메뉴만 넣은후 vu를 증가시키며 포화지점을 찾는 방식으로 진행했습니다.

### 테스트 대상: 메뉴 목록 조회 API (GET /api/menus)

Redis Cache가 적용된 API로 동시 사용자 증가에 따른 성능 변화를 측정했습니다.

### 결과 요약(Grafana 대시보드 기준)

| 동시 사용자 | 에러율 | 평균 응답시간 | P95 |
|---|---|---|---|
| 300명 | 0.00% | 2.56ms | 4.18ms |
| 3,000명 | 0.00% | 2.04ms | 3.72ms |
| 8,000명 | 0.00% | 3.34ms | 6.62ms |
| 9,000명 | 9.82% | 18.29ms | 6.58ms |

포화 지점: 8,000명 ~ 9,000명 사이
8,000명까지는 에러 0%, 평균 3ms대로 안정적으로 처리되었습니다.
Redis Cache 적용으로 대규모 트래픽에서도 낮은 응답시간을 유지했습니다.

### 8,000명 테스트 결과

<img width="1000" height="400" alt="image" src="https://github.com/user-attachments/assets/168ee976-625c-4970-ae1d-5abeb02bd764" />


| 지표 | 값 |
|---|---|
| Total Transaction | 80,282 |
| Error Rate | 0.00% |
| Min | 0.49ms |
| Avg | 2.32ms |
| P90 | 4.11ms |
| P95 | 5.17ms |
| Max | 46.83ms |
| 최대 RPS | 600 req/s |

### 9,000명 테스트 결과

<img width="1000" height="400" alt="image" src="https://github.com/user-attachments/assets/a232eacd-a6d8-4c7b-b2f3-95f1e633c8a0" />


| 지표 | 값 |
|---|---|
| Total Transaction | 87,782 |
| Error Rate | 9.82% |
| Min | 0.50ms |
| Avg | 18.29ms |
| P90 | 4.81ms |
| P95 | 6.58ms |
| Max | 1.68s |
| 최대 RPS | 541 req/s |

### 결론

8,000명까지는 에러율 0%, 평균 응답시간 2.32ms로 안정적으로 처리되었습니다.
9,000명부터 에러율 9.82%, 평균 응답시간 18.29ms로 급격히 성능이 저하되었습니다.
포화 지점은 8,000명 ~ 9,000명 사이로 확인되었습니다.
