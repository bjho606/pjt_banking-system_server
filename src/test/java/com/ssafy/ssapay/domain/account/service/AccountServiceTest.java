package com.ssafy.ssapay.domain.account.service;

import static com.ssafy.ssapay.util.Fixture.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ssafy.ssapay.config.TestConfig;
import com.ssafy.ssapay.domain.account.dto.request.CheckBalanceRequest;
import com.ssafy.ssapay.domain.account.dto.response.BalanceResponse;
import com.ssafy.ssapay.domain.account.dto.response.accountNumberResponse;
import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.auth.dto.internal.LoginUser;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.global.error.type.BadRequestException;
import com.ssafy.ssapay.util.TestTransactionService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@DisplayName("계좌 기본 CRUD 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@Import(TestConfig.class)
class AccountServiceTest {
    private final AccountService accountService;
    private final TestTransactionService testService;
    private final EntityManager em;

    @Autowired
    public AccountServiceTest(AccountService accountService, TestTransactionService testService, EntityManager em) {
        this.accountService = accountService;
        this.testService = testService;
        this.em = em;
    }

    @BeforeEach
    void setUp() {
        testService.truncateTables();
    }

    @Test
    void 계좌를_생성할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        testService.persist(user);
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername());
        // when
        accountNumberResponse response = accountService.createAccount(loginUser);
        // then
        String accountNumber = response.accountNumber();
        Account account = em.find(Account.class, accountNumber);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(account).isNotNull();
        s.assertThat(account.getUser().getId()).isEqualTo(user.getId());
        s.assertAll();
    }

    @Test
    void 계좌_잔액을_확인할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        account.addBalance(new BigDecimal(10000));
        testService.persist(user, account);
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername());
        CheckBalanceRequest request = new CheckBalanceRequest(account.getAccountNumber());
        // when
        BalanceResponse response = accountService.checkBalance(request, loginUser);
        // then
        BigDecimal balance = response.balance();
        BigDecimal expected = new BigDecimal(10000);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(balance).isNotNull();
        s.assertThat(balance.compareTo(expected)).isEqualTo(0);
        s.assertAll();
    }

    @Test
    void 계좌_입금을_할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        testService.persist(user, account);
        // when
        accountService.deposit(account.getAccountNumber(), new BigDecimal(10000));
        // then
        Account result = em.find(Account.class, account.getAccountNumber());
        BigDecimal expected = new BigDecimal(10000);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(result.getBalance().compareTo(expected)).isEqualTo(0);
        s.assertAll();
    }

    @Test
    void 계좌_출금을_할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        account.addBalance(new BigDecimal(10000));
        testService.persist(user, account);
        // when
        accountService.withdraw(account.getAccountNumber(), new BigDecimal(5000));
        // then
        Account result = em.find(Account.class, account.getAccountNumber());
        BigDecimal expected = new BigDecimal(5000);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(result.getBalance().compareTo(expected)).isEqualTo(0);
        s.assertAll();
    }

    @Test
    void 계좌_잔액이_부족하면_출금_시_예외가_발생한다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        account.addBalance(new BigDecimal(10000));
        testService.persist(user, account);
        // when then
        assertThrows(BadRequestException.class, () ->
                accountService.withdraw(account.getAccountNumber(), new BigDecimal(20000)));
    }

    @Test
    void 계좌_송금을_할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account fromAccount = new Account(user, "11111111");
        Account toAccount = new Account(user, "22222222");
        fromAccount.addBalance(new BigDecimal(10000));
        testService.persist(user, fromAccount, toAccount);
        // when
        accountService.transfer(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), new BigDecimal(5000));
        // then
        Account resultFromAccount = em.find(Account.class, fromAccount.getAccountNumber());
        Account toFromAccount = em.find(Account.class, toAccount.getAccountNumber());
        BigDecimal fromAccountBalanceExpected = new BigDecimal(5000);
        BigDecimal toAccountBalanceExpected = new BigDecimal(5000);

        SoftAssertions s = new SoftAssertions();
        s.assertThat(resultFromAccount.getBalance().compareTo(fromAccountBalanceExpected)).isEqualTo(0);
        s.assertThat(toFromAccount.getBalance().compareTo(toAccountBalanceExpected)).isEqualTo(0);
        s.assertAll();
    }

    @Test
    void 계좌를_삭제할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        testService.persist(user, account);
        // when
        accountService.deleteAccount(account.getAccountNumber());
        // then
        Account result = em.find(Account.class, account.getAccountNumber());
        assertThat(result.isDeleted()).isTrue();
    }
}