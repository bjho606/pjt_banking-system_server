package com.ssafy.dongsanbu.domain.point.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ssafy.dongsanbu.domain.auth.dto.LoginDto;
import com.ssafy.dongsanbu.domain.auth.mapper.AuthMapper;
import com.ssafy.dongsanbu.domain.point.dto.PointInsertDto;
import com.ssafy.dongsanbu.domain.point.mapper.PointMapper;
import com.ssafy.dongsanbu.domain.user.entity.User;
import com.ssafy.dongsanbu.domain.user.mapper.UserMapper;
import com.ssafy.dongsanbu.util.DBUtils;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("비관적 락을 사용한 포인트 사용 시 동시성 이슈 발생 테스트")
class PointServiceV3Test {
    private final PointServiceV3 pointServiceV3;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final PointMapper pointMapper;

    @Autowired
    public PointServiceV3Test(PointServiceV3 pointServiceV3,
                              UserMapper userMapper,
                              AuthMapper authMapper,
                              PointMapper pointMapper) {
        this.pointServiceV3 = pointServiceV3;
        this.userMapper = userMapper;
        this.authMapper = authMapper;
        this.pointMapper = pointMapper;
    }

    @Value("${db.database}")
    private String database;
    @Value("${db.username}")
    private String username;
    @Value("${db.password}")
    private String password;

    @BeforeEach
    void setUp() {
        DBUtils.truncate("point_record", database, username, password);
        DBUtils.truncate("member", database, username, password);
    }

    @Test
    void 비관적_락을_사용하여_동시성_이슈를_해결한다() throws InterruptedException {
        // given
        int point = 10000;
        User user = User.builder()
                .username("test")
                .password("test")
                .nickname("test")
                .email("test@test.com")
                .authority("USER")
                .point(point)
                .build();
        userMapper.registUser(user);
        User savedUser = authMapper.findByUsernameAndPassword(new LoginDto("test", "test"));

        PointInsertDto pointInsertDto = new PointInsertDto(savedUser.getId(), point);
        pointMapper.insertPoint(pointInsertDto);
        // when
        int cnt = 200;
        int usePoint = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(cnt);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < cnt; ++i) {
            executorService.submit(() -> {
                try {
                    pointServiceV3.usePoint(savedUser.getId(), usePoint);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        System.out.println("successCount = " + successCount);
        System.out.println("failCount = " + failCount);

        //then
        User resultUser = userMapper.findById(savedUser.getId());
        System.out.println(resultUser);
        int pointRecordCount = DBUtils.countAll("point_record", database, username, password);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(resultUser.getPoint()).isZero();
        softly.assertThat(pointRecordCount).isEqualTo(point / usePoint + 1);
        softly.assertAll();
    }
}