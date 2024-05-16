package com.ssafy.ssapay.domain.account.service;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.account.repository.AccountRepository;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.repository.UserRepository;
import com.ssafy.ssapay.util.DBUtils;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("계좌 기본 CRUD 테스트")
class AccountServiceTest {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountServiceTest(AccountService accountService, AccountRepository accountRepository, UserRepository userRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Value("${db.database}")
    private String database;
    @Value("${db.username}")
    private String username;
    @Value("${db.password}")
    private String password;

    void setUp(String tableName) {
        DBUtils.truncate(tableName, database, username, password);
    }

    @Test
    @DisplayName("계좌 생성 테스트")
    void testCreateAccount() {
        User user = new User();
        user.setId(1L);
        Account createdAccount = accountService.createAccount(1L);

        assertNotNull(createdAccount);
        assertEquals(user.getId(), createdAccount.getUser().getId());
    }

//    @Test
//    @DisplayName("계좌 잔액 확인 테스트")
//    void testCheckBalance() {
//        Account account = new Account();
//        account.setId(1L);
//        account.setBalance(BigDecimal.valueOf(1000));
//
//        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
//
//        BigDecimal balance = accountService.checkBalance(1L);
//
//        assertNotNull(balance);
//        assertEquals(BigDecimal.valueOf(1000), balance);
//    }

    @Test
    @DisplayName("계좌 입금 테스트")
    void testDeposit() {
        Account createdAccount = accountService.createAccount(1L);

        accountService.deposit(createdAccount.getId(), BigDecimal.valueOf(1000));

        assertEquals(BigDecimal.valueOf(1000), accountService.checkBalance(createdAccount.getId()));
    }

    @Test
    @DisplayName("계좌 출금 테스트")
    void testWithdraw() {
        Account createdAccount = accountService.createAccount(1L);
        accountService.deposit(createdAccount.getId(), BigDecimal.valueOf(1000));

        accountService.withdraw(createdAccount.getId(), BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(500), accountService.checkBalance(createdAccount.getId()));
    }

    @Test
    @DisplayName("계좌 송금 테스트")
    void testTransfer() {
        Account createdFromAccount = accountService.createAccount(1L);
        accountService.deposit(createdFromAccount.getId(), BigDecimal.valueOf(1000));

        Account createdToAccount = accountService.createAccount(1L);
        accountService.deposit(createdToAccount.getId(), BigDecimal.valueOf(500));

        accountService.transfer(createdFromAccount.getId(), createdToAccount.getId(), BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(800), accountService.checkBalance(createdFromAccount.getId()));
        assertEquals(BigDecimal.valueOf(700), accountService.checkBalance(createdToAccount.getId()));
    }

//    @Test
//    @DisplayName("계좌 삭제 테스트")
//    void testDeleteAccount() {
//        Account createdAccount = accountService.createAccount(1L);
//
//        accountService.deleteAccount(createdAccount.getId());
//
//        assertTrue(createdAccount.isDeleted());
//    }
}