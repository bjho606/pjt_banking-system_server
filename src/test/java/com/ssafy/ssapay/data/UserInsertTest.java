package com.ssafy.ssapay.data;

import static com.ssafy.ssapay.util.Fixture.createUser;

import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserInsertTest {
    private final EntityManager em;
    private final UserRepository userRepository;

    @Autowired
    public UserInsertTest(EntityManager em, UserRepository userRepository) {
        this.em = em;
        this.userRepository = userRepository;
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
            users.add(createUser("user" + start, "user" + start, "user" + start + "@test.com"));
        }
        userRepository.saveAll(users);
    }
}
