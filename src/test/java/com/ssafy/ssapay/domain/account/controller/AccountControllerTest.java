package com.ssafy.ssapay.domain.account.controller;

import static com.ssafy.ssapay.util.Fixture.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@SpringBootTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("[계좌 컨트롤러 테스트]")
class AccountControllerTest {
    private final MockMvc mockMvc;
    private final EntityManager em;

    @Autowired
    public AccountControllerTest(MockMvc mockMvc,
                                 EntityManager em) {
        this.mockMvc = mockMvc;
        this.em = em;
    }

    @Test
    void 계좌를_생성할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");

        em.persist(user);
        Long userId = user.getId();

        em.flush();
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
                .andExpect(jsonPath("$.accountId")
                        .isNumber());
    }

    @Test
    void 계좌_잔액을_확인할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        account.addBalance(new BigDecimal(10000));

        em.persist(user);
        em.persist(account);
        em.flush();
        // when then
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/account/balance")
                .content("{\"accountId\": " + account.getId() + "}")
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
        em.persist(user);
        em.persist(account);
        em.flush();
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/account/deposit")
                .content("{\"accountId\": " + account.getId() + ", \"amount\": 10000}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        // then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        em.flush();
        assertThat(account.getBalance()).isEqualTo(new BigDecimal(10000));
    }

    @Test
    void 계좌_출금을_할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        account.addBalance(new BigDecimal(10000));
        em.persist(user);
        em.persist(account);
        em.flush();
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/account/withdraw")
                .content("{\"accountId\": " + account.getId() + ", \"amount\": 5000}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        // then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        em.flush();
        assertThat(account.getBalance()).isEqualTo(new BigDecimal(5000));
    }

    @Test
    void 계좌_송금을_할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account fromAccount = new Account(user, "11111111");
        Account toAccount = new Account(user, "22222222");
        fromAccount.addBalance(new BigDecimal(10000));
        em.persist(user);
        em.persist(fromAccount);
        em.persist(toAccount);
        em.flush();
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/account/transfer")
                .content("{\"fromAccountId\": " + fromAccount.getId() + ", \"toAccountId\": " + toAccount.getId()
                        + ", \"amount\": 5000}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        // then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        SoftAssertions s = new SoftAssertions();
        s.assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal(5000));
        s.assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal(5000));
        s.assertAll();
    }

    @Test
    void 계좌를_삭제할_수_있다() throws Exception {
        // given
        User user = createUser("test", "test", "test@test.com");
        Account account = new Account(user, "11111111");
        em.persist(user);
        em.persist(account);
        em.flush();
        // when
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/v1/account")
                .content("{\"accountId\": " + account.getId() + "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.displayName());
        // then
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(account.isDeleted());
    }
}