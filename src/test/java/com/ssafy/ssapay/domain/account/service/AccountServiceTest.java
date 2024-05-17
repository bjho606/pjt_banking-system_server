package com.ssafy.ssapay.domain.account.service;

import static com.ssafy.ssapay.util.Fixture.createUser;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ssafy.ssapay.domain.account.dto.response.AccountIdResponse;
import com.ssafy.ssapay.domain.account.dto.response.BalanceResponse;
import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.account.repository.AccountRepository;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("계좌 기본 CRUD 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@SpringBootTest
class AccountServiceTest {
    private final AccountService accountService;
    private final EntityManager em;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountServiceTest(AccountService accountService, EntityManager em, AccountRepository accountRepository, UserRepository userRepository) {
        this.accountService = accountService;
        this.em = em;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Test
    void 계좌를_생성할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        em.persist(user);
        Long userId = user.getId();
        // when
        AccountIdResponse response = accountService.createAccount(userId);
        // then
        Long accountId = response.accountId();
        Account account = em.find(Account.class, accountId);

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
        em.persist(user);
        em.persist(account);
        // when
        BalanceResponse response = accountService.checkBalance(account.getId());
        // then
        BigDecimal balance = response.balance();

        SoftAssertions s = new SoftAssertions();
        s.assertThat(balance).isNotNull();
        s.assertThat(balance).isEqualTo(new BigDecimal(10000));
        s.assertAll();
    }

    @Test
    void 계좌_입금을_할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        em.persist(user);
        em.persist(account);
        // when
        accountService.deposit(account.getId(), new BigDecimal(10000));
        // then
        SoftAssertions s = new SoftAssertions();
        s.assertThat(account.getBalance()).isEqualTo(new BigDecimal(10000));
        s.assertAll();
    }

    @Test
    void 계좌_출금을_할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        account.addBalance(new BigDecimal(10000));
        em.persist(user);
        em.persist(account);
        // when
        accountService.withdraw(account.getId(), new BigDecimal(5000));
        // then
        SoftAssertions s = new SoftAssertions();
        s.assertThat(account.getBalance()).isEqualTo(new BigDecimal(5000));
        s.assertAll();
    }

    @Test
    void 계좌_잔액이_부족하면_출금_시_예외가_발생한다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        account.addBalance(new BigDecimal(10000));
        em.persist(user);
        em.persist(account);
        // when then
        assertThrows(RuntimeException.class, () -> accountService.withdraw(account.getId(), new BigDecimal(20000)));
    }

    @Test
    void 계좌_송금을_할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account fromAccount = new Account(user, "11111111");
        Account toAccount = new Account(user, "22222222");
        fromAccount.addBalance(new BigDecimal(10000));
        em.persist(user);
        em.persist(fromAccount);
        em.persist(toAccount);
        // when
        accountService.transfer(fromAccount.getId(), toAccount.getId(), new BigDecimal(5000));
        // then
        SoftAssertions s = new SoftAssertions();
        s.assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal(5000));
        s.assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal(5000));
        s.assertAll();
    }

    @Test
    void 계좌를_삭제할_수_있다() {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        em.persist(user);
        em.persist(account);
        // when
        accountService.deleteAccount(account.getId());
        // then
        assertTrue(account.isDeleted());
    }

}