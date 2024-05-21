package com.ssafy.ssapay.data;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.infra.repository.write.AccountWriteRepository;
import com.ssafy.ssapay.infra.repository.write.UserWriteRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountInsertTest {
    private final AccountWriteRepository accountRepository;
    private final UserWriteRepository userRepository;

    @Autowired
    public AccountInsertTest(AccountWriteRepository accountRepository,
                             UserWriteRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Test
    void test() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            System.out.println(user.getUsername());
            insertAccounts(user);
        }
    }

    public void insertAccounts(User user) {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            accounts.add(new Account(user, "mj-" + user.getUsername() + i));
        }
        accountRepository.saveAll(accounts);
    }
}
