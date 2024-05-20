package com.ssafy.ssapay.data;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.infra.repository.read.AccountReadRepository;
import com.ssafy.ssapay.infra.repository.read.UserReadRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

//@SpringBootTest
class AccountInsertTest {
    private final AccountReadRepository accountReadRepository;
    private final UserReadRepository userRepository;

    @Autowired
    public AccountInsertTest(AccountReadRepository accountReadRepository,
                             UserReadRepository userRepository) {
        this.accountReadRepository = accountReadRepository;
        this.userRepository = userRepository;
    }

    //    @Test
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
        accountReadRepository.saveAll(accounts);
    }
}
