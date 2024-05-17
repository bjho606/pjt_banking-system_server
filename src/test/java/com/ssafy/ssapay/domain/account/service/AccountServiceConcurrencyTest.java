package com.ssafy.ssapay.domain.account.service;

import com.ssafy.ssapay.domain.account.dto.response.AccountIdResponse;
import com.ssafy.ssapay.domain.account.dto.response.BalanceResponse;
import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.account.repository.AccountRepository;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ssafy.ssapay.util.Fixture.createUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("계좌 기본 CRUD 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
class AccountServiceConcurrencyTest {
    private final AccountService accountService;
    private final EntityManager em;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountServiceConcurrencyTest(AccountService accountService, EntityManager em, AccountRepository accountRepository, UserRepository userRepository) {
        this.accountService = accountService;
        this.em = em;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Test
    void 동시에_입금_요청을_보낸다() throws InterruptedException {
        int totalCount = 200;
        User user = createUser("user", "user", "user@email.com");
        Account account = new Account(user, String.valueOf(user.getId()));
        userRepository.save(user);
        accountRepository.save(account);
        //when
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch countDownLatch = new CountDownLatch(totalCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        for (int i = 0; i < totalCount; ++i) {
            executorService.submit(() -> {
                try {
                    accountService.deposit(account.getId(), new BigDecimal("10000"));
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
        Account result = em.find(Account.class, account.getId());
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(successCount.get()).isEqualTo(totalCount);
        softly.assertThat(result.getBalance()).isEqualTo(new BigDecimal("2000000.00"));
        softly.assertAll();
    }


    @Test
    void 동시에_출금_요청을_보낸다() throws InterruptedException{
        // given
        User user = createUser("a", "test", "test@test.com");
        userRepository.save(user);

        Account account = new Account(user, "11111110");
        account.addBalance(new BigDecimal(10000));
        accountRepository.save(account);

        // when
        int cnt = 200;
        BigDecimal useBalance = new BigDecimal(100);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(cnt);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < cnt; ++i) {
            executorService.submit(() -> {
                try {
                    accountService.withdraw(account.getId(), useBalance);

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

        // then
        Account updatedAccount = em.find(Account.class, account.getId());

        SoftAssertions s = new SoftAssertions();
        s.assertThat(updatedAccount.getBalance()).isEqualTo(new BigDecimal("0.00"));
        s.assertAll();
    }

    @Test
    void 동시에_송금_요청을_보낸다() throws InterruptedException{
        // given
        User user1 = createUser("test1", "test", "test@test.com");
        User user2 = createUser("test2", "test", "test@test.com");
        userRepository.save(user1);
        userRepository.save(user2);

        Account account1 = new Account(user1, "11111110");
        account1.addBalance(new BigDecimal(50000));
        Account account2 = new Account(user2, "11111111");
        account2.addBalance(new BigDecimal(50000));
        accountRepository.save(account1);
        accountRepository.save(account2);

        // when
        int cnt = 200;
        BigDecimal balance1 = new BigDecimal(200);
        BigDecimal balance2 = new BigDecimal(100);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(cnt);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < cnt; ++i) {
            executorService.submit(() -> {
                try {
                    accountService.transfer(account1.getId(), account2.getId(), balance1);
                    accountService.transfer(account2.getId(), account1.getId(), balance2);

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

        // then
        Account updatedAccount1 = em.find(Account.class, account1.getId());
        Account updatedAccount2 = em.find(Account.class, account2.getId());

        SoftAssertions s = new SoftAssertions();
        s.assertThat(updatedAccount1.getBalance()).isEqualTo(new BigDecimal("30000.00"));
        s.assertThat(updatedAccount2.getBalance()).isEqualTo(new BigDecimal("70000.00"));
        s.assertAll();
    }

}