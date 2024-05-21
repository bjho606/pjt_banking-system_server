package com.ssafy.ssapay.data;

import static com.ssafy.ssapay.util.Fixture.createUser;

import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.infra.repository.write.UserWriteRepository;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserInsertTest {
    private final UserWriteRepository userWriteRepository;

    @Autowired
    public UserInsertTest(UserWriteRepository userRepository) {
        this.userWriteRepository = userRepository;
    }

    @Test
    void test() {
        //given
        for (int i = 0; i < 100000; i += 10000) {
            System.out.println("i: " + i);
            insert10000Users(i);
        }
    }

    private void insert10000Users(int start) {
        ArrayList<User> users = new ArrayList<>();
        for (int i = start; i < start + 10000; ++i) {
            users.add(createUser("user" + i, "user" + i, "user" + i + "@test.com"));
        }
        userWriteRepository.saveAll(users);
    }
}
