package com.ssafy.ssapay.domain.account.service;

import com.ssafy.ssapay.domain.account.dto.response.AccountIdResponse;
import com.ssafy.ssapay.domain.account.dto.response.AccountResponse;
import com.ssafy.ssapay.domain.account.dto.response.AllAccountsResponse;
import com.ssafy.ssapay.domain.account.dto.response.BalanceResponse;
import com.ssafy.ssapay.domain.account.dto.response.PaymentRecordResponse;
import com.ssafy.ssapay.domain.account.dto.response.RecordsInPeriodResponse;
import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.global.error.type.BadRequestException;
import com.ssafy.ssapay.infra.repository.read.AccountReadRepository;
import com.ssafy.ssapay.infra.repository.write.AccountWriteRepository;
import com.ssafy.ssapay.infra.repository.write.PaymentRecordWriteRepository;
import com.ssafy.ssapay.infra.repository.write.UserWriteRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountService {
    private final AccountReadRepository accountReadRepository;

    private final UserWriteRepository userWriteRepository;
    private final AccountWriteRepository accountWriteRepository;
    private final PaymentRecordWriteRepository paymentRecordWriteRepository;

    // 계좌 생성
    @Transactional
    public AccountIdResponse createAccount(Long userId) {
        User user = userWriteRepository.findById(userId)
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

        Account newAccount = accountWriteRepository.save(account);

        return new AccountIdResponse(newAccount.getId());
    }

    // 계좌 잔액 확인
    public BalanceResponse checkBalance(Long accountId) {
        Account account = accountReadRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Account not found"));
        return new BalanceResponse(account.getBalance());
    }

    // 계좌 입금
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        Account account = accountWriteRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new BadRequestException("Account not found"));

        account.addBalance(amount);

        PaymentRecord paymentRecord = new PaymentRecord(account, amount);
        paymentRecordWriteRepository.save(paymentRecord);

        log.debug("deposit {} {}", accountId, amount);
    }

    // 계좌 출금
    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        Account account = accountWriteRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new BadRequestException("Account not found"));

        if (account.isLess(amount)) {
            throw new BadRequestException("잔액 부족");
        }

        account.substractBalance(amount);

        PaymentRecord paymentRecord = new PaymentRecord(account, amount.negate());
        paymentRecordWriteRepository.save(paymentRecord);

        log.debug("withdraw {} {}", accountId, amount);
    }

    // 계좌 송금
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account fromAccount;
        Account toAccount;
        if (fromAccountId < toAccountId) {
            fromAccount = accountWriteRepository.findByIdForUpdate(fromAccountId)
                    .orElseThrow(() -> new BadRequestException("From account not found"));
            toAccount = accountWriteRepository.findByIdForUpdate(toAccountId)
                    .orElseThrow(() -> new BadRequestException("To account not found"));
        } else {
            toAccount = accountWriteRepository.findByIdForUpdate(toAccountId)
                    .orElseThrow(() -> new BadRequestException("To account not found"));
            fromAccount = accountWriteRepository.findByIdForUpdate(fromAccountId)
                    .orElseThrow(() -> new BadRequestException("From account not found"));
        }

        if (fromAccount.isLess(amount)) {
            throw new BadRequestException("잔액 부족");
        }

        fromAccount.substractBalance(amount);
        toAccount.addBalance(amount);

        accountWriteRepository.save(fromAccount);
        accountWriteRepository.save(toAccount);

        PaymentRecord paymentRecord = new PaymentRecord(fromAccount, toAccount, amount);
        paymentRecordWriteRepository.save(paymentRecord);

        log.debug("transfer {} to {} {}", fromAccountId, toAccountId, amount);
    }

    // 계좌 삭제
    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = accountWriteRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new BadRequestException("Account not found"));
        account.delete();

        log.debug("delete {} to {} {}", accountId);
    }

    // 유저의 전체 계좌 조회
    public AllAccountsResponse checkAllAccounts(Long userId) {
        List<Account> accounts = accountReadRepository.findAllAccountByUserId(userId);
        List<AccountResponse> accountInfos = accounts.stream()
                .map(AccountResponse::from)
                .toList();

        return new AllAccountsResponse(accountInfos);
    }

    // 계좌 잔액 기간별 확인
    public RecordsInPeriodResponse checkPaymentRecordByPeriod(Long accountId, LocalDate startDate, LocalDate endDate) {
//        System.out.println(startDate);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
//        System.out.println(start);
        List<PaymentRecord> records = accountReadRepository.findByIdAndPeriod(accountId, start, end);
        List<PaymentRecordResponse> recordInfos = records.stream()
                .map(PaymentRecordResponse::from)
                .toList();

        return new RecordsInPeriodResponse(recordInfos);
    }

}
