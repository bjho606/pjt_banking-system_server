package com.ssafy.ssapay.domain.account.service;

import com.ssafy.ssapay.domain.account.dto.response.AccountIdResponse;
import com.ssafy.ssapay.domain.account.dto.response.BalanceResponse;
import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.account.repository.AccountRepository;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import com.ssafy.ssapay.domain.payment.repository.PaymentRecordRepository;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Random;

import com.ssafy.ssapay.global.aop.OptimisticRetry;
import com.ssafy.ssapay.global.aop.PessimisticRetry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final UserRepository userRepository;

    // 계좌 생성
    @Transactional
    public AccountIdResponse createAccount(Long userId) {
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
        Account account = new Account(user, newAccountNumber);

        log.debug("createAccount {} with {}", userId, newAccountNumber);

        Account newAccount = accountRepository.save(account);

        return new AccountIdResponse(newAccount.getId());
    }

    // 계좌 잔액 확인
    public BalanceResponse checkBalance(Long accountId) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return new BalanceResponse(account.getBalance());
    }

    // 계좌 입금
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.addBalance(amount);

        PaymentRecord paymentRecord = new PaymentRecord(account, amount);
        paymentRecordRepository.save(paymentRecord);

        log.debug("deposit {} {}", accountId, amount);
    }

    // 계좌 출금
    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.isLess(amount)) {
            throw new RuntimeException("잔액 부족");
        }

        account.substractBalance(amount);

        PaymentRecord paymentRecord = new PaymentRecord(account, amount.negate());
        paymentRecordRepository.save(paymentRecord);

        log.debug("withdraw {} {}", accountId, amount);
    }

    // 계좌 송금
    @Transactional
    // [방법1. 데드락 발생 시, 재시도 하는 방법]
//    @PessimisticRetry
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
//        Account fromAccount = accountRepository.findById(fromAccountId)
//                .orElseThrow(() -> new RuntimeException("From account not found"));
//        Account toAccount = accountRepository.findById(toAccountId)
//                .orElseThrow(() -> new RuntimeException("To account not found"));

        // [방법2. 애초에 데드락이 발생하지 않도록, 무조건 더 작은 accountId가 락을 먼저 획득하도록 하는 방법]
        Account fromAccount;
        Account toAccount;
        if (fromAccountId < toAccountId) {
            fromAccount = accountRepository.findByIdForUpdate(fromAccountId)
                    .orElseThrow(() -> new RuntimeException("From account not found"));
            toAccount = accountRepository.findByIdForUpdate(toAccountId)
                    .orElseThrow(() -> new RuntimeException("To account not found"));
        } else {
            toAccount = accountRepository.findByIdForUpdate(toAccountId)
                    .orElseThrow(() -> new RuntimeException("To account not found"));
            fromAccount = accountRepository.findByIdForUpdate(fromAccountId)
                    .orElseThrow(() -> new RuntimeException("From account not found"));
        }

        if (fromAccount.isLess(amount)) {
            throw new RuntimeException("잔액 부족");
        }

        fromAccount.substractBalance(amount);
        toAccount.addBalance(amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        PaymentRecord paymentRecord = new PaymentRecord(fromAccount, toAccount, amount);
        paymentRecordRepository.save(paymentRecord);

        log.debug("transfer {} to {} {}", fromAccountId, toAccountId, amount);
    }

    // 계좌 삭제
    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.delete();

        log.debug("delete {} to {} {}", accountId);
    }
}
