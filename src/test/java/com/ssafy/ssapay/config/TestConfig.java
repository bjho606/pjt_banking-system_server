package com.ssafy.ssapay.config;

import com.ssafy.ssapay.util.TestTransactionService;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    private final EntityManager em;

    public TestConfig(EntityManager em) {
        this.em = em;
    }

    @Bean
    public TestTransactionService testTransactionService() {
        return new TestTransactionService(em);
    }
}
