# 1. Introduction
## Team
- 서울 19반 박민지
- 서울 19반 변재호

## Skill & Tool
- java
- springboot
- mybatis
- mysql
- spring aop

## How to Run
- resources/privacy.yml 추가
  ```yml
  db:
  host: [host정보]
  port: 3306
  database: [db 이름]
  username: [user 이름]
  password: [비밀번호]

  image:
  upload-locations: classpath:/static/images/
  ```
- DB 추가 (개인 요청^^)

## 구현 결과
- 트랜잭션 처리
  - 동시성 유발
  - 낙관적 락을 이용한 동시성 해결 방법
  - 비관적 락을 이용한 동시성 해결 방법

---
# 2. Project
## 목적
트랜잭션을 사용할 때 발생할 수 있는 문제와, 안전하게 관리하기 위한 방법을 알아보고 실습한다.
1. 동시성 문제 발생 상황
2. 낙관적 락을 통한 트랜잭션 처리
3. 비관적 락을 통한 트랜잭션 처리

---
## 시나리오
임의의 사용자가 최초에 10000 포인트를 가지고, 1번 요청 시 100 포인트가 차감되도록 총 200회의 요청을 보내려 한다. <br>
결과는 100번의 요청만 허락되어 최종 포인트는 0원이어야 하고, 나머지 100번의 요청은 잔액부족으로 실패해야 한다.<br>
이때, 최대 10번까지 동시 요청이 가능한 경우 결과는 어떻게 될까?

---
## 구현 기능
### 트랜잭션 처리
#### ERD
![스크린샷 2024-05-13 오전 12.04.15.png](..%2F..%2F..%2F..%2F..%2F..%2F..%2FDownloads%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-05-13%20%EC%98%A4%EC%A0%84%2012.04.15.png)
- Member 테이블 : 멤버 정보 + 멤버가 보유하고 있는 총 포인트(point) 정보
  - 동시성 문제가 발생하는 주요 원인
- Point_Record 테이블 : member_id를 FK로 가지고 있는 포인트 사용 내역
- Point_Record2 테이블 : member_id를 FK로 가지고 있지 않은 포인트 사용 내역 (낙관적 락을 사용하기 위해)


#### 1. 동시성 문제 
> 일단 동시성 문제를 고의로 만들어보자
- PointServiceV1.java
    ```java
    @Service
    @RequiredArgsConstructor
    @Transactional(readOnly = true)
    public class PointServiceV1 {
        private final UserMapper userMapper;
        private final PointMapper pointMapper;
    
        @Transactional
        public void usePoint(int userId, int pointAmount) {
            User user = userMapper.findById(userId);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            pointMapper.insertPoint(new PointInsertDto(userId, -pointAmount));
    
            pointMapper.insertPoint(new PointInsertDto(userId, -pointAmount));
    
            user.usePoint(pointAmount);
            userMapper.updatePoint(user);
    
        }
    }
    ```
- PointServiceV1Test.java
    ```java
    @DisplayNameGeneration(ReplaceUnderscores.class)
    @SpringBootTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @DisplayName("포인트 사용 시 동시성 이슈 발생 테스트")
    class PointServiceV1Test {
    private final PointServiceV1 pointServiceV1;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final PointMapper pointMapper;

    @Autowired
    public PointServiceV1Test(PointServiceV1 pointServiceV1,
                              UserMapper userMapper,
                              AuthMapper authMapper,
                              PointMapper pointMapper) {
        this.pointServiceV1 = pointServiceV1;
        this.userMapper = userMapper;
        this.authMapper = authMapper;
        this.pointMapper = pointMapper;
    }

    @Value("${db.database}")
    private String database;
    @Value("${db.username}")
    private String username;
    @Value("${db.password}")
    private String password;

    @BeforeEach
    void setUp() {
        DBUtils.truncate("point_record", database, username, password);
        DBUtils.truncate("member", database, username, password);
    }

    @Test
    void 동시성_이슈가_발생하여_테스트가_실패한다() throws InterruptedException {
        // given
        int point = 10000;
        User user = User.builder()
                .username("test")
                .password("test")
                .nickname("test")
                .email("test@test.com")
                .authority("USER")
                .point(point)
                .build();
        userMapper.registUser(user);
        User savedUser = authMapper.findByUsernameAndPassword(new LoginDto("test", "test"));

        PointInsertDto pointInsertDto = new PointInsertDto(savedUser.getId(), point);
        pointMapper.insertPoint(pointInsertDto);
        // when
        int cnt = 200;
        int usePoint = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(cnt);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < cnt; ++i) {
            executorService.submit(() -> {
                try {
                    pointServiceV1.usePoint(savedUser.getId(), usePoint);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        System.out.println("successCount = " + successCount);
        System.out.println("failCount = " + failCount);

        //then
        User resultUser = userMapper.findById(savedUser.getId());
        System.out.println(resultUser);
        int pointRecordCount = DBUtils.countAll("point_record", database, username, password);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(resultUser.getPoint()).isZero();
        softly.assertThat(pointRecordCount).isEqualTo(point / usePoint + 1);
        softly.assertAll();
        }
    }
    ```
- Test Result
![스크린샷 2024-05-13 오전 12.50.05.png](..%2F..%2F..%2F..%2F..%2F..%2F..%2FDownloads%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-05-13%20%EC%98%A4%EC%A0%84%2012.50.05.png)
  - 데드락 발생
  
#### Why?
- MySql은 FK를 가지는 테이블의 삽입,수정,삭제가 일어날 때, 해당 제약 조건을 위반하는지 확인하기 위해 관련 레코드들에 공유 잠금 (S lock)을 설정한다.
  - S Lock : 다른 트랜잭션의 데이터 '변경'을 막음.
  - 여러 트랜잭션이 동시에 S Lock 을 얻을 수 있음.
- 여러 트랜잭션이 S Lock을 얻고 있을 때, 서로의 배타 잠금 (X Lock)을 얻을 수 없는 상태가 되어 데드락 발생
  - X Lock : 한번에 하나의 트랜잭션만이 '쓰기' 작업을 수행할 수 있음.
- 정리하자면, 하나의 트랜잭션에서 일어나는 일은
  1. Member 테이블에 m.id 확인
  2. Point_Record 테이블에 새로운 행(포인트 변동 정보) 추가 -> `S Lock 획득`
  3. Member 테이블에 point 갱신 -> `X Lock` 대기 <br>
  => 다른 트랜잭션에서 설정한 S Lock 때문에 데드락 발생
  ![deadlock1.png](..%2F..%2F..%2F..%2F..%2F..%2F..%2FDownloads%2Fdeadlock1.png)

#### 2. 동시성 문제를 어떻게 해결하지? Part 1
> 낙관적 락 (Optimistic Lock)
> > 여러 트랜잭션 간 충돌이 일어나지 않을 것이라 가정
> > - Version을 통한 관리
- 우선 연관관계를 없애자 (Point_Record2 테이블 사용)
- Member 테이블에 `version` 컬럼 추가
- 기존 userMapper.updatePoint()를 updatePointWithVersion()으로 바꾼다.
  ```xml
  <update id="updatePointWithVersion" parameterType="Map">
    UPDATE member
    SET point = #{point},
        version = #{originalVersion}+1
    WHERE id = #{id}
    AND version = #{originalVersion}
  </update> 
  ```
- 여러 트랜잭션에서 member의 데이터를 읽어왔을 때, version이 맞지 않으면 rollback 된다. 
  ![optimistic.png](..%2F..%2F..%2F..%2F..%2F..%2F..%2FDownloads%2Foptimistic.png)
- 이렇게 rollback 된 데이터는 예외처리를 통해 재시도하는 로직을 구현해야 완전한 동시성 해결이 가능하다.
  - 재시도 방법 : retry 하는 AOP 구현
  - OptimisticLockRetryAspect.java
    ```java
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    @Aspect
    @Component
    public class OptimisticLockRetryAspect {
    private static final int MAX_RETRIES = 20;
    private static final int RETRY_DELAY_MS = 100;
  
      @Pointcut("@annotation(Retry)")
      public void retry() {
      }
  
      @Around("retry()")
      public Object retryOptimisticLock(ProceedingJoinPoint joinPoint) throws Throwable {
          Exception exceptionHolder = null;
          for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
              try {
                  return joinPoint.proceed();
              } catch (IllegalStateException e) {
                  exceptionHolder = e;
                  Thread.sleep(RETRY_DELAY_MS);
              }
          }
          throw exceptionHolder;
      }
    }
    ```
- Test Result![스크린샷 2024-05-13 오전 1.21.11.png](..%2F..%2F..%2F..%2F..%2F..%2F..%2FDownloads%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-05-13%20%EC%98%A4%EC%A0%84%201.21.11.png)

#### 3. 동시성 문제를 어떻게 해결하지? Part 2
> 비관적 락 (Pessimistic Lock)
> > 여러 트랜잭션 간 충돌이 일어날 것이라 가정
> > - Select ... For Update 문 => 특정 row에 배타 락 걸기
- 기존 userMapper.findById()를 findByIdForUpdate() 로 바꾼다.
  ```xml
  <select id="findByIdForUpdate" parameterType="int" resultType="user">
    SELECT *
    FROM member
    WHERE id = #{id}
    For Update
  </select>
  ```
- select for update 문으로 해당 row에 배타락이 걸리기 때문에, 다른 트랜잭션들은 이 락이 풀릴 때까지 대기해야 한다.
- Test Result
![스크린샷 2024-05-13 오전 1.29.38.png](..%2F..%2F..%2F..%2F..%2F..%2F..%2FDownloads%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-05-13%20%EC%98%A4%EC%A0%84%201.29.38.png)

#### 낙관적 락 vs 비관적 락
- 낙관적 락
  - 동시성 문제를 db 레벨이 아닌, `어플리케이션 레벨`에서 처리 (version을 통해)
  - 따라서, 버전 충돌로 인해 실패할 경우, 직접 예외처리로 재시도 로직을 구현해야 함
  - 요청이 들어온 순서대로 X. 재시도 타이밍에 따라 O => 요청 순서대로 처리하는 상황에 부적합
- 비관적 락
  - 단일 DB 환경에만 적용 가능
  - 반드시 한 트랜잭션이 완료될 때까지 나머지가 대기해야하므로 대기시간이 오래 걸림 => 요청 수가 많아지면 대기시간이 길어짐

---
# 프로젝트 소감
### 박민지

### 변재호
```
항상 이론으로만 공부하던 트랜잭션 처리를 직접해볼 수 있어서 좋았습니다.
트랜잭션을 처리하는 방법으로 이외에도 메시지큐, 분산잠금 등이 있다는데 이후에 이 부분도 다루어볼 수 있다면 좋을 것 같습니다.
```