package com.ssafy.ssapay.domain.account.controller;

import static com.ssafy.ssapay.util.Fixture.createUser;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.ssafy.ssapay.config.TestConfig;
import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.util.TestTransactionService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest
@SuppressWarnings("NonAsciiCharacters")
@Import(TestConfig.class)
@DisplayName("[계좌 컨트롤러 테스트]")
class AccountControllerTest {
    private final MockMvc mockMvc;
    private final EntityManager em;
    private final TestTransactionService testTransactionService;

    @Autowired
    public AccountControllerTest(MockMvc mockMvc,
                                 EntityManager em,
                                 TestTransactionService testTransactionService) {
        this.mockMvc = mockMvc;
        this.em = em;
        this.testTransactionService = testTransactionService;
    }

    @BeforeEach
    void setUp() {
        testTransactionService.truncateTables();
    }

    @Test
    void 계좌를_생성할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        testTransactionService.persist(user);

        Long userId = user.getId();
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/account")
                .content("{\"userId\": " + userId + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        //when then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.accountNumber")
                        .isNumber());
    }

    @Test
    void 계좌_잔액을_확인할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        account.addBalance(new BigDecimal(10000));
        testTransactionService.persist(user, account);
        // when then
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/account/balance")
                .content("{\"accountNumber\": " + account.getAccountNumber() + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        //when then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.balance")
                        .value(10000));
    }

    @Test
    void 계좌_입금을_할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        testTransactionService.persist(user, account);
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/account/deposit")
                .content("{\"accountNumber\": " + account.getAccountNumber() + ", \"amount\": 10000}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        // then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 계좌_출금을_할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        account.addBalance(new BigDecimal(10000));
        testTransactionService.persist(user, account);
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/account/withdraw")
                .content("{\"accountNumber\": " + account.getAccountNumber() + ", \"amount\": 5000}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        // then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 계좌_송금을_할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account fromAccount = new Account(user, "11111111");
        Account toAccount = new Account(user, "22222222");
        fromAccount.addBalance(new BigDecimal(10000));
        testTransactionService.persist(user, fromAccount, toAccount);
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/account/transfer")
                .content("{\"fromaccountNumber\": " + fromAccount.getAccountNumber() + ", \"toaccountNumber\": " + toAccount.getAccountNumber()
                        + ", \"amount\": 5000}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        // then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void 계좌를_삭제할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        testTransactionService.persist(user, account);
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/v1/account")
                .content("{\"accountNumber\": " + account.getAccountNumber() + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        // then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}