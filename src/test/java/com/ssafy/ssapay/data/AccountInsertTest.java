package com.ssafy.ssapay.data;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.account.repository.AccountRepository;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccountInsertTest {
    private final EntityManager em;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountInsertTest(EntityManager em,
                             AccountRepository accountRepository,
                             UserRepository userRepository) {
        this.em = em;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Test
    void test() {
        //given
        List<User> users = userRepository.findAll();
        for (int i = 0; i < 100000; i += 1000) {
            System.out.println("i: " + i);
            insert10000Account(i, users);
        }
    }

    private void insert10000Account(int start, List<User> users) {
        ArrayList<Account> accounts = new ArrayList<>();
        for (int i = start; i < start + 1000; ++i) {
            User user = users.get(i);

            for (int j = 0; j < 10; ++j) {
                accounts.add(new Account(user, "USER" + i + "ACCOUNT" + j));
            }
        }
        accountRepository.saveAll(accounts);
    }
}
