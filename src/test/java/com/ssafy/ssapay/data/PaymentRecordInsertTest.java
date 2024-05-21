package com.ssafy.ssapay.data;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import com.ssafy.ssapay.infra.repository.write.PaymentRecordWriteRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentRecordInsertTest {
    private final PaymentRecordWriteRepository paymentRecordWriteRepository;
    private final EntityManager em;

    @Autowired
    public PaymentRecordInsertTest(PaymentRecordWriteRepository paymentRecordWriteRepository,
                                   EntityManager em) {
        this.paymentRecordWriteRepository = paymentRecordWriteRepository;
        this.em = em;
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    @Test
    void test() {
        //give
        List<Account> accounts = em.createQuery("SELECT a FROM Account a where a.user.id >= 1 and a.user.id <= 1000",
                Account.class).getResultList();
        for (int i = 0; i < 100_0000; i += 10000) {
            System.out.println("i: " + i);
            insert10000PaymentRecord(i, accounts);
        }
    }

    private void insert10000PaymentRecord(int start, List<Account> accounts) {
        List<PaymentRecord> paymentRecords = new ArrayList<>();

        for (int i = start; i < start + 10000; ++i) {
            int idx = getRandomNumberInRange(0, accounts.size() - 1);
            Account toAccount = accounts.get(idx);
            String toAccountNumber = toAccount.getAccountNumber();

            PaymentRecord paymentRecord = null;
            int num = getRandomNumberInRange(1, 35);
            if (num > 20) {
                paymentRecord = new PaymentRecord(null, toAccountNumber, new BigDecimal(1000));
            } else if (num > 12) {
                if (toAccount.isLess(new BigDecimal(1000))) {
                    paymentRecord = new PaymentRecord(null, toAccountNumber, new BigDecimal(1000));
                } else {
                    paymentRecord = new PaymentRecord(null, toAccountNumber, new BigDecimal(-1000));
                }
            } else {
                int fromIdx = getRandomNumberInRange(0, accounts.size() - 1);
                Account from = accounts.get(fromIdx);
                if (fromIdx == idx || from.isLess(new BigDecimal(1000))) {
                    paymentRecord = new PaymentRecord(null, toAccountNumber, new BigDecimal(1000));
                } else {
                    paymentRecord = new PaymentRecord(from.getAccountNumber(), toAccountNumber, new BigDecimal(1000));
                }
            }

            paymentRecords.add(paymentRecord);
        }

        paymentRecordWriteRepository.saveAll(paymentRecords);
    }
}
