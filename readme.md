# 1. Introduction

## Team

- 서울 19반 변재호
- 서울 19반 박민지

## Skill & Tool

- java
- springboot
- jpa
- mysql (with replication)
- GCP (Google Cloud Platform)
- AWS EC2
- kafka
- k6

## Structure

- DB Schema
  ![erd](./uploads/ssapay_erd.png)
- Architecture Diagram
  ![arc diagram](./uploads/ssapay_architecture_diagram.png)

## Settings

- resources/privacy.yml 추가

  ```yml
  db:
    host: [host정보]
    port: 3306
    database: [db 이름]
    username: [user 이름]
    password: [비밀번호]

  master:
    host: [host정보]
    port: 3306
    database: [db 이름]
    username: [user 이름]
    password: [비밀번호]

  slave:
    host: [host정보]
    port: 3306
    database: [db 이름]
    username: [user 이름]
    password: [비밀번호]

  image:
    upload-locations: classpath:/static/images/
  ```

---

# 2. Project

## 목적

안전한 결제 시스템을 구현하기 위해 다음과 같은 요구사항들을 해결한다.

1. 단일 서버에서 동시성 문제를 해결한다.
2. 분산 서버에서 동시성 문제를 해결한다.
3. 성능과 고가용성을 위해 Read/Write로 분리된 데이터베이스를 구축한다.

- OAuth 기반 인증 방식을 알아보고 구현한다.

## 구현 기능

1. 입금 : 사용자는 돈을 입금할 수 있다.
2. 출금 : 사용자는 돈을 출금할 수 있다.
    - 사용자가 출금하려는 돈이 계좌에 없으면, '잔액부족'이 뜬다.
3. 송금 : 사용자는 현 계좌에서 다른 계좌로 돈을 송금할 수 있다.
    - 사용자가 송금하려는 돈이 계좌에 없으면, '잔액부족'이 뜬다.
    - 사용자는 같은 결제 시스템을 사용하는 계좌로 송금할 수 있다.
    - 사용자는 다른 결제 시스템을 사용하는 계좌로 송금할 수 있다.
4. OAuth 로그인 : 구글 소셜 로그인을 통해 회원가입 및 로그인을 할 수 있다.

### OAuth

![oauth](./uploads/oauth.png)

1. 사용자가 소셜 로그인 버튼을 클릭한다.
2. 클라이언트가 사용자에 302 accounts.google.com/o/oauth2/v2/auth Response를 보낸다.
3. 사용자가 권한 승인 코드 발급 요청을 보낸다.
    - 파라미터 : state, response_type, client_id, scope, redirect_url
4. ~5)
5. 사용자가 구글에 로그인되어있지 않다면, 구글에 로그인한다.
6. 사용자가 구글 로그인에 성공하면, 권한 승인 코드(`code`)를 발급하고 302 response를 보낸다.
    - redirect url의 파라미터에 `code`가 추가되어 있음
7. 사용자가 클라이언트에 소셜 로그인을 요청한다.
    - 파라미터 : state, code, scope
8. ~9)
9. 클라이언트는 권한 서버에 `accessToken` 발급을 요청하고 응답 받는다.
10. ~11)
11. 클라이언트는 리소스 서버에 `accessToken`을 사용해 사용자의 정보를 요청하고 응답 받는다.
12. 클라이언트는 사용자 정보를 기반으로 회원가입/로그인을 처리한다.
    - `CustomOAuth2UserService.loadUser()`로 최초 로그인 시에는 회원가입으로, 아닐 시에는 로그인으로 처리
13. 클라이언트는 사용자에게 인증 관련 토큰/세션을 발급하며 로그인 성공 url redirect response를 보낸다.
    - `onAuthenticationSuccess()`로 사용자의 JWT 토큰을 발급하고, 로그인 성공 시 리다이렉트 url로 redirect한다.

## 구현 비기능

### 1. 단일 서버에서 트랜잭션 처리

> 단일 서버 내에서 한 계좌에서의 입금, 출금, 송금 요청이 동시다발적으로 들어올 때,<br>
> 트랜잭션의 원자성이 보장되어야 한다.<br>
> => 낙관적 락 vs 비관적 락 중 <b>비관적 락</b>을 선택함

#### 문제 발생

송금 요청 시, 데드락이 발생

#### 문제 발생 원인

![send deadlock reason](./uploads/send_deadlock_reason.jpg)

#### 해결

배타락을 획득하는 순서를 일정하게 관리 => 계좌 번호가 작은 순부터 락을 획득하도록 하여 순환 대기 발생하지 않도록 코드 수정

- 기존 코드

  ```java
  // 계좌 송금
  @Transactional
  public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
      Account fromAccount = accountRepository.findById(fromAccountId)
              .orElseThrow(() -> new RuntimeException("From account not found"));
      Account toAccount = accountRepository.findById(toAccountId)
              .orElseThrow(() -> new RuntimeException("To account not found"));

      if (fromAccount.isLess(amount)) {
          throw new RuntimeException("잔액 부족");
      }

      ...
  }
  ```

- 수정된 코드

  ```java
  // 계좌 송금
  @Transactional
  public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
      Account fromAccount;
      Account toAccount;
      // 계좌 번호가 작은 순으로 락을 획득하도록 변경
      if (fromAccountId < toAccountId) {
      fromAccount = accountRepository.findById(fromAccountId)
      .orElseThrow(() -> new RuntimeException("From account not found"));
      toAccount = accountRepository.findById(toAccountId)
      .orElseThrow(() -> new RuntimeException("To account not found"));
      } else {
      toAccount = accountRepository.findById(toAccountId)
      .orElseThrow(() -> new RuntimeException("To account not found"));
      fromAccount = accountRepository.findById(fromAccountId)
      .orElseThrow(() -> new RuntimeException("From account not found"));
      }

      ...
  }
  ```

  ![send deadlock solution](./uploads/send_deadlock_solution.jpg)

### 2. 분산 서버에서 트랜잭션 처리 1

> 분산 서버(서로 다른 결제 시스템) 사이에서 한 계좌에서 다른 계좌로의 송금 요청이 동시다발적으로 들어올 때,<br>
> 트랜잭션의 원자성이 보장되어야 한다.

#### 문제 발생

A시스템의 a1 계좌와 B시스템의 b1 계좌 사이에 송금하는 요청이 동시에 들어오면 데드락 발생

#### 문제 발생 원인

![send between deadlock reason](./uploads/send_between_deadlock_reason.jpg)

#### 해결

마찬가지로 배타락을 획득하는 순서를 뒤바꾸어, 획득 순서가 꼬이지 않도록 코드 수정

- 기존 코드

  ```java
  private void processWithOuterSystem(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
      // 1. 자기 계좌에 배타락을 획득한 상태
      Account fromAccount = accountWriteRepository.findByAccountNumberForUpdate(fromAccountNumber)
              .orElseThrow(() -> new BadRequestException("From account not found"));

      fromAccount.substractBalance(amount);
      PaymentRecord paymentRecord = new PaymentRecord(fromAccount.getAccountNumber(), amount.negate());
      paymentRecordWriteRepository.save(paymentRecord);

      // 2. 상대 계좌에서 배타락을 또 획득한 상태 => 데드락
      paymentClient.requestTransfer(fromAccountNumber, toAccountNumber, amount);
  }

  ```

- 수정된 코드

  ```java
  private void processWithOuterSystem(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
  // 1. 해당 계좌번호가 존재하는지만 확인
  if (!accountRepository.existsByAccountNumber(fromAccountNumber)) {
  throw new BadRequestException("From account not found");
  }

  // 2. 상대 계좌부터 락을 획득하여 처리하도록 변경
  paymentClient.requestTransfer(fromAccountNumber, toAccountNumber, amount);

  // 3. 이후 자기 계좌의 락을 획득하여 처리
  PaymentRecord paymentRecord = new PaymentRecord(fromAccountNumber, toAccountNumber, amount.negate());
  Account fromAccount = accountRepository.findByAccountNumberForUpdate(fromAccountNumber)
          .orElseThrow(() -> new BadRequestException("From account not found"));
  fromAccount.substractBalance(amount);
  paymentRecordRepository.save(paymentRecord);
  }
  ```

![send_between_deadlock_solution](./uploads/send_between_deadlock_solution.jpg)

### 3.분산 서버에서 트랜잭션 처리 2

> 분산 서버(서로 다른 결제 시스템) 사이에서 한 계좌에서 다른 계좌로의 송금 장애나 예외처리로 인해 실패할 경우,<br>
> 트랜잭션이 rollback 되어야 한다. <br>
> => Saga Pattern 도입 (그 중 Choreography 방식)

![send_between_exception](./uploads/send_between_exception.jpg)

#### Saga Pattern (Choreography 방식)
- 이벤트 pub/sub (publisher/subscriber) 을 활용하여 통신하는 방법
- 프로세스를 진행하다가 중간에 실패(장애, 예외처리)하는 경우가 발생하면 `보상 트랜잭션 이벤트`를 발행하여 원상태로 되돌리는 방법
- 대표적으로 kafka 로 구현할 수 있다.
  ![kafka](./uploads/kafka_progress.jpg)
  - 실패 포인트는 어디든 될 수 있다. 결국 실패한 곳에서부터 보상 트랜잭션이 발동하여 이전 변경사항을 되돌린다.
      ```java
      private void processWithOuterSystem(String fromAccountNumber,
                                          String toAccountNumber,
                                          BigDecimal amount) {
          String uuid = UUID.randomUUID().toString();
          // 중간 과정이 모두 성공하면 그대로 진행한다.
          try {
              if (!accountRepository.existsByAccountNumber(fromAccountNumber)) {
                  throw new BadRequestException("From account not found");
              }

              paymentClient.requestTransfer(uuid, fromAccountNumber, toAccountNumber, amount);

              PaymentRecord paymentRecord = new PaymentRecord(uuid, fromAccountNumber, toAccountNumber, amount.negate());
              Account fromAccount = accountRepository.findByAccountNumberForUpdate(fromAccountNumber)
                      .orElseThrow(() -> new BadRequestException("From account not found"));
              fromAccount.substractBalance(amount);
              paymentRecordRepository.save(paymentRecord);
              generateRandomException();
          }
          // 중간에 실패하는 경우, 보상 트랜잭션 이벤트를 발행한다. 
          catch (Exception e) {
              paymentProducer.transferRollback(uuid);   // 보상 이벤트 발행
              throw new SsapayException("Cannot transfer money to outer system", e);
          }
      }
      ```
    - PaymentProducer 에서는 `transfer-rollback` 토픽 이벤트를 생성한다.
    ```java
    @Component
    @RequiredArgsConstructor
    @Slf4j
    public class PaymentProducer {
        private final KafkaTemplate kafkaTemplate;
    
        public void transferRollback(String uuid) {
            log.info("======[Send Transfer Rollback] {}======", uuid);
            kafkaTemplate.send("transfer-rollback", uuid);
        }
    }
    ```
    - PaymentRollbackConsumer에서는 `transfer-rollback` 토픽을 구독하며, 보상 트랜잭션을 실행시킨다.
    - 여기서 보상 트랜잭션이란, 계좌의 잔액을 원상태로 복구시키고, 트랜잭션 시작 이후의 거래 내역을 삭제하는 것이다.
    ```java
    @Slf4j
    @Component
    @RequiredArgsConstructor
    public class PaymentRollbackConsumer {
        private final AccountService accountService;
    
        @KafkaListener(topics = "transfer-rollback", groupId = "group-01")
        public void rollback(String uuid) {
            log.error("======[Rollback] {}======", uuid);
            accountService.rollbackTransfer(uuid);
        }
    }
    ```

### 4. Read/Write DB 분리 (DB Replication)

> 하나의 DB에서 모든 read,write를 처리하기보다,<br>
> read와 write를 처리하는 db를 분리하여 성능과 가용성을 높인다.

![dp replication](./uploads/db_replication.png)

#### 과정
1. mysql 설치 및 계정 설정
    ```bash
    sudo apt-get update
    sudo apt-get install mysql-server
    sudo systemctl start mysql
    sudo systemctl enable mysql
    
    sudo mysql_secure_installation
    sudo mysql -u root
    ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '새로운_비밀번호';
    flush privileges;
    
    create database 'DB 이름';
    ```
2. mysql 설정 (master, slave)
    ```bash
    sudo vim /etc/mysql/mysql.conf.d/mysqld.cnf

    ##############소스 서버
    [mysqld]
    user            = mysql
    # pid-file      = /var/run/mysqld/mysqld.pid
    # socket        = /var/run/mysqld/mysqld.sock
    # port          = 3306
    # datadir       = /var/lib/mysql
    gtid_mode=ON
    enforce_gtid_consistency=ON
    server_id=1111
    log_bin=mysql
    
    ##############레플리카 서버
    [mysqld]
    user            = mysql
    # pid-file      = /var/run/mysqld/mysqld.pid
    # socket        = /var/run/mysqld/mysqld.sock
    # port          = 3306
    # datadir       = /var/lib/mysql
    gtid_mode=ON
    enforce_gtid_consistency=ON
    server_id=2222
    relay_log=relay
    relay_log_purge=ON
    read_only
    log_slave_updates
   ```
3. 복제 계정 설정
   ```bash
   create user 'repl_user'@'%' identified by '비밀번호';
   grant replication slave on *.* to 'repl_user'@'%';
   flush privileges;
   ````
4. 데이터 추가
5. 복제 시작
   ```bash
   source /home/.../source_data.sql;

   change replication source to source_host='복제할 host', source_port=3306, source_user='repl_user', source_password='비밀번호', source_auto_position=1, get_source_public_key=1;
    
   start replica;
   show replica status \\G
   ```

#### 성능 테스트


---
# 프로젝트 소감

### 변재호

```
트랜잭션의 원자성을 보장하고, 트랜잭션이 실패했을 때 처리하는 보상 이벤트에 대해 많이 배울 수 있었습니다.
특히 평소에 기능적 개발만을 하다가 이런 비기능 요소를 깊이 탐구해볼 수 있는 뜻 깊은 시간이었습니다.

데드락을 처리하는 방식으로 어플리케이션 단위와 db 단위, 그리고 네트워크 단위로 처리할 수 있다는게 신기했고, 다음에는 분산락에 대해서도 더 공부해보고 싶습니다.
또한, db의 복제를 처음 다뤄보며 실제 성능에도 좋은 영향을 주는 것을 보고 더 다양한 인프라 구성에도 도전해보고 싶었습니다.
```

### 박민지

```
트랜잭션 처리에 대해 많은 부분을 배울 수 있었습니다.
프로젝트에 적용하지는 않았으나 propagation, @Transactional의 롤백 처리 등 트랜잭션을 사용하는 방법이 훨씬 다양하다는 것을 배웠습니다.

분산 트랜잭션과 같이 네트워크를 타는 것들은 항상 처리에 주의해야 한다는 점도 배웠습니다. 
보상 트랜잭션을 이용한 롤백이 실패하는 경우는 또 어떻게 처리해야할지도 고민이 되어, 끝나고도 더 찾아볼 것 같습니다.

비기능과 아키텍처를 중심으로 프로젝트를 진행하였습니다.
아키텍처를 다루려다 보니 여러 서버를 사용하게 되어 CI/CD, docker 등 환경 설정을 편하게 하는 것이 참 필요하구나, 라는 점을 깨달았습니다.
지금까지는 필요성을 크게 인식하지 못했는데, 이번 프로젝트로 필요성을 체감하였습니다.

“비기능”을 목표로 프로젝트를 진행한 것은 처음이었습니다. 
이번에는 아키텍처와 함께 설정하여 “기술”을 통해 비기능을 구현하였으나, 
앞으로 프로젝트에서는 코드 수준의 비기능 등 더 세부적으로 나누어 구현해보고 싶습니다!
```
