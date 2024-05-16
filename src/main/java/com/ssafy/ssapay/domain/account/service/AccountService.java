package com.ssafy.ssapay.domain.account.service;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.account.repository.AccountRepository;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import com.ssafy.ssapay.domain.payment.repository.PaymentRecordRepository;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private UserRepository userRepository;

    // 계좌 생성
    @Transactional
    public Account createAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 8자리 계좌번호 생성
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int digit = random.nextInt(10); // 0부터 9까지의 숫자 생성
            sb.append(digit);
        }

        String newAccountNumber = sb.toString();

        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(newAccountNumber);
//        account.setBalance(BigDecimal.valueOf(0));

        return accountRepository.save(account);
    }

    // 계좌 잔액 확인
    public BigDecimal checkBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return account.getBalance();
    }

    // 계좌 입금
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(amount));

        accountRepository.save(account);

        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setFromAccount(account);
        paymentRecord.setAmount(amount);

        paymentRecordRepository.save(paymentRecord);
    }

    // 계좌 출금
    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("잔액 부족");
        }

        account.setBalance(account.getBalance().subtract(amount));

        accountRepository.save(account);

        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setFromAccount(account);
        paymentRecord.setAmount(amount.multiply(BigDecimal.valueOf(-1)));

        paymentRecordRepository.save(paymentRecord);
    }

    // 계좌 송금
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("From account not found"));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("To account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("잔액 부족");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setFromAccount(fromAccount);
        paymentRecord.setToAccount(toAccount);
        paymentRecord.setAmount(amount);

        paymentRecordRepository.save(paymentRecord);
    }

    // 계좌 삭제
    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setDeleted(true);
        System.out.println(account);

        accountRepository.save(account);
    }
}
