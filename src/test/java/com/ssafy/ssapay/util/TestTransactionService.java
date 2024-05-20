package com.ssafy.ssapay.util;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

public class TestTransactionService {
    private final EntityManager em;

    public TestTransactionService(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void truncateTables() {
        em.createNativeQuery("delete from payment_record").executeUpdate();
        em.createNativeQuery("delete from account").executeUpdate();
        em.createNativeQuery("delete from user").executeUpdate();
    }

    @Transactional
    public void persist(Object... objects) {
        for (Object object : objects) {
            em.persist(object);
        }
    }
}
