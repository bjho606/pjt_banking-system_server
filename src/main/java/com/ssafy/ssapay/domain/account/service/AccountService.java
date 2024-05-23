package com.ssafy.ssapay.domain.account.service;

import com.ssafy.ssapay.domain.account.dto.response.AccountResponse;
import com.ssafy.ssapay.domain.account.dto.response.AllAccountsResponse;
import com.ssafy.ssapay.domain.account.dto.response.BalanceResponse;
import com.ssafy.ssapay.domain.account.dto.response.PaymentRecordResponse;
import com.ssafy.ssapay.domain.account.dto.response.RecordsInPeriodResponse;
import com.ssafy.ssapay.domain.account.dto.response.accountNumberResponse;
import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.global.error.type.BadRequestException;
import com.ssafy.ssapay.global.error.type.SsapayException;
import com.ssafy.ssapay.infra.payment.PaymentClient;
import com.ssafy.ssapay.infra.payment.PaymentProducer;
import com.ssafy.ssapay.infra.repository.AccountRepository;
import com.ssafy.ssapay.infra.repository.PaymentRecordRepository;
import com.ssafy.ssapay.infra.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountService {
    private static Random random = new Random();

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PaymentRecordRepository paymentRecordRepository;

    private final PaymentClient paymentClient;
    private final PaymentProducer paymentProducer;

    @Value("${account.number.prefix}")
    private String ACCOUNT_NUMBER_PREFIX;

    // 계좌 생성
    @Transactional
    public accountNumberResponse createAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

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

        return new accountNumberResponse(newAccount.getAccountNumber());
    }

    // 계좌 잔액 확인
    public BalanceResponse checkBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));
        return new BalanceResponse(account.getBalance());
    }

    // 계좌 입금
    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));

        account.addBalance(amount);

        PaymentRecord paymentRecord = new PaymentRecord(account.getAccountNumber(), amount);
        paymentRecordRepository.save(paymentRecord);

        log.debug("deposit {} {}", accountNumber, amount);
    }

    // 계좌 출금
    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));

        if (account.isLess(amount)) {
            throw new BadRequestException("잔액 부족");
        }

        account.substractBalance(amount);

        PaymentRecord paymentRecord = new PaymentRecord(account.getAccountNumber(), amount.negate());
        paymentRecordRepository.save(paymentRecord);

        log.debug("withdraw {} {}", accountNumber, amount);
    }

    // 계좌 송금
    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (toAccountNumber.startsWith(ACCOUNT_NUMBER_PREFIX)) {
            processWithInnerSystem(fromAccountNumber, toAccountNumber, amount);
        } else {
            processWithOuterSystem(fromAccountNumber, toAccountNumber, amount);
        }
    }

    public static void generateRandomException() {
        int num = random.nextInt(2) + 1;
//        int num = 1;
        if (num == 1) {
            throw new RuntimeException("Error occured!");
        }
    }

    private void processWithInnerSystem(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        Account fromAccount;
        Account toAccount;

        if (fromAccountNumber.compareTo(toAccountNumber) > 0) {
            fromAccount = accountRepository.findByAccountNumberForUpdate(fromAccountNumber)
                    .orElseThrow(() -> new BadRequestException("From account not found"));
            toAccount = accountRepository.findByAccountNumberForUpdate(toAccountNumber)
                    .orElseThrow(() -> new BadRequestException("To account not found"));
        } else {
            toAccount = accountRepository.findByAccountNumberForUpdate(toAccountNumber)
                    .orElseThrow(() -> new BadRequestException("To account not found"));
            fromAccount = accountRepository.findByAccountNumberForUpdate(fromAccountNumber)
                    .orElseThrow(() -> new BadRequestException("From account not found"));
        }

        if (fromAccount.isLess(amount)) {
            throw new BadRequestException("잔액 부족");
        }

        fromAccount.substractBalance(amount);
        toAccount.addBalance(amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        PaymentRecord paymentRecord = new PaymentRecord(fromAccount.getAccountNumber(),
                toAccount.getAccountNumber(),
                amount);
        paymentRecordRepository.save(paymentRecord);

        log.debug("transfer {} to {} {}", fromAccountNumber, toAccountNumber, amount);
    }

    private void processWithOuterSystem(String fromAccountNumber,
                                        String toAccountNumber,
                                        BigDecimal amount) {
        String uuid = UUID.randomUUID().toString();
        try {
            if (!accountRepository.existsByAccountNumber(fromAccountNumber)) {
                throw new BadRequestException("From account not found");
            }

            paymentClient.requestTransfer(uuid, fromAccountNumber, toAccountNumber, amount);

            PaymentRecord paymentRecord = new PaymentRecord(uuid, fromAccountNumber, toAccountNumber, amount.negate());
            Account fromAccount = accountRepository.findByAccountNumberForUpdate(fromAccountNumber)
                    .orElseThrow(() -> new BadRequestException("From account not found"));
            fromAccount.substractBalance(amount);
            paymentRecordRepository.save(paymentRecord);
            generateRandomException();
        } catch (Exception e) {
            paymentProducer.transferRollback(uuid);
            throw new SsapayException("Cannot transfer money to outer system", e);
        }
    }

    //다른 서비스에서 송금 요청
    @Transactional
    public void transferExternal(String uuid,
                                 String fromAccountNumber,
                                 String toAccountNumber,
                                 BigDecimal amount) {
        Account account = accountRepository.findByAccountNumberForUpdate(toAccountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));

        account.addBalance(amount);

        PaymentRecord paymentRecord = new PaymentRecord(uuid,
                fromAccountNumber,
                account.getAccountNumber(),
                amount);
        paymentRecordRepository.save(paymentRecord);
    }

    @Transactional
    public void rollbackTransfer(String uuid) {
        PaymentRecord paymentRecord = paymentRecordRepository.findByUuid(uuid)
                .orElseThrow(() -> new BadRequestException("Payment record not found"));

        String toAccountNumber = paymentRecord.getToAccountNumber();
        BigDecimal amount = paymentRecord.getAmount();

        Account account = accountRepository.findByAccountNumberForUpdate(toAccountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));
        account.substractBalance(amount);

        paymentRecordRepository.delete(paymentRecord);
    }

    // 계좌 삭제
    @Transactional
    public void deleteAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));
        account.delete();

        log.debug("delete {} to {} {}", accountNumber);
    }

    // 유저의 전체 계좌 조회
    public AllAccountsResponse checkAllAccounts(Long userId) {
        List<Account> accounts = accountRepository.findAllAccountByUserId(userId);
        List<AccountResponse> accountInfos = accounts.stream()
                .map(AccountResponse::from)
                .toList();

        return new AllAccountsResponse(accountInfos);
    }

    // 계좌 잔액 기간별 확인
    public RecordsInPeriodResponse checkPaymentRecordByPeriod(String accountNumber, LocalDate startDate,
                                                              LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<PaymentRecord> records = accountRepository.findByAccountNumberAndPeriod(accountNumber, start, end);
        List<PaymentRecordResponse> recordInfos = records.stream()
                .map(PaymentRecordResponse::from)
                .toList();

        return new RecordsInPeriodResponse(recordInfos);
    }
}
