package com.ssafy.ssapay.domain.account.service;

import com.ssafy.ssapay.domain.account.dto.request.AccountDeleteRequest;
import com.ssafy.ssapay.domain.account.dto.request.CheckBalanceRequest;
import com.ssafy.ssapay.domain.account.dto.request.DepositRequest;
import com.ssafy.ssapay.domain.account.dto.request.TransferRequest;
import com.ssafy.ssapay.domain.account.dto.request.WithdrawRequest;
import com.ssafy.ssapay.domain.account.dto.request.checkPaymentRecordByPeriod;
import com.ssafy.ssapay.domain.account.dto.response.AccountResponse;
import com.ssafy.ssapay.domain.account.dto.response.AllAccountsResponse;
import com.ssafy.ssapay.domain.account.dto.response.BalanceResponse;
import com.ssafy.ssapay.domain.account.dto.response.PaymentRecordResponse;
import com.ssafy.ssapay.domain.account.dto.response.RecordsInPeriodResponse;
import com.ssafy.ssapay.domain.account.dto.response.accountNumberResponse;
import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.account.implement.AccountNumberGenerator;
import com.ssafy.ssapay.domain.account.implement.AccountReader;
import com.ssafy.ssapay.domain.account.implement.AccountValidator;
import com.ssafy.ssapay.domain.account.implement.AccountWriter;
import com.ssafy.ssapay.domain.auth.dto.internal.LoginUser;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.implementation.UserReader;
import com.ssafy.ssapay.global.error.type.BadRequestException;
import com.ssafy.ssapay.global.error.type.SsapayException;
import com.ssafy.ssapay.global.util.CommonUtil;
import com.ssafy.ssapay.infra.payment.PaymentClient;
import com.ssafy.ssapay.infra.payment.PaymentProducer;
import com.ssafy.ssapay.infra.repository.AccountRepository;
import com.ssafy.ssapay.infra.repository.PaymentRecordRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccountService {
    private final AccountNumberGenerator accountNumberGenerator;

    private final UserReader userReader;
    private final AccountReader accountReader;
    private final AccountWriter accountWriter;
    private final AccountValidator accountValidator;
    private final AccountRepository accountRepository;
    private final PaymentRecordRepository paymentRecordRepository;

    private final PaymentClient paymentClient;
    private final PaymentProducer paymentProducer;

    @Value("${account.number.prefix}")
    private String ACCOUNT_NUMBER_PREFIX;

    public AccountService(AccountReader accountReader, AccountRepository accountRepository,
                          PaymentRecordRepository paymentRecordRepository,
                          UserReader userReader,
                          AccountWriter accountWriter,
                          AccountValidator accountValidator,
                          PaymentClient paymentClient,
                          PaymentProducer paymentProducer) {
        this.accountReader = accountReader;
        this.accountWriter = accountWriter;
        this.accountValidator = accountValidator;
        this.accountNumberGenerator = new AccountNumberGenerator();
        this.accountRepository = accountRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.userReader = userReader;
        this.paymentClient = paymentClient;
        this.paymentProducer = paymentProducer;
    }

    // 계좌 생성
    public accountNumberResponse createAccount(LoginUser loginUser) {
        User user = userReader.getUserById(loginUser.id());

        String accountNumber = accountNumberGenerator.generateAccountNumber(ACCOUNT_NUMBER_PREFIX);
        Account account = new Account(user, accountNumber);

        Account newAccount = accountWriter.appendNewAccount(account);
        return new accountNumberResponse(newAccount.getAccountNumber());
    }

    @Transactional(readOnly = true)
    public BalanceResponse checkBalance(CheckBalanceRequest request, LoginUser loginUser) {
        Account account = accountReader.getAccountByAccountNumber(request.accountNumber());
        accountValidator.validAccountOwner(account, loginUser);

        return new BalanceResponse(account.getBalance());
    }

    // 계좌 입금
    @Transactional
    public void deposit(DepositRequest request, LoginUser loginUser) {
        String accountNumber = request.accountNumber();
        BigDecimal amount = request.amount();

        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));
        accountValidator.validAccountOwner(account, loginUser);

        account.addBalance(amount);

        PaymentRecord paymentRecord = new PaymentRecord(account.getAccountNumber(), amount);
        paymentRecordRepository.save(paymentRecord);

        log.debug("deposit {} {}", accountNumber, amount);
    }

    // 계좌 출금
    @Transactional
    public void withdraw(WithdrawRequest request, LoginUser loginUser) {
        String accountNumber = request.accountNumber();
        BigDecimal amount = request.amount();

        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));
        accountValidator.validAccountOwner(account, loginUser);

        if (account.isLess(amount)) {
            throw new BadRequestException("잔액 부족");
        }

        account.substractBalance(amount);

        PaymentRecord paymentRecord = new PaymentRecord(account.getAccountNumber(), amount.negate());
        paymentRecordRepository.save(paymentRecord);
    }

    // 계좌 송금
    @Transactional
    public void transfer(TransferRequest request, LoginUser loginUser) {
        String fromAccountNumber = request.fromAccountNumber();
        String toAccountNumber = request.toAccountNumber();
        BigDecimal amount = request.amount();

        if (toAccountNumber.startsWith(ACCOUNT_NUMBER_PREFIX)) {
            processWithInnerSystem(fromAccountNumber, toAccountNumber, amount, loginUser);
        } else {
            processWithOuterSystem(fromAccountNumber, toAccountNumber, amount, loginUser);
        }
    }

    private void processWithInnerSystem(String fromAccountNumber,
                                        String toAccountNumber,
                                        BigDecimal amount,
                                        LoginUser loginUser) {
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
        accountValidator.validAccountOwner(fromAccount, loginUser);

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
    }

    private void processWithOuterSystem(String fromAccountNumber,
                                        String toAccountNumber,
                                        BigDecimal amount, LoginUser loginUser) {
        String uuid = UUID.randomUUID().toString();
        try {
            Account fromAccount = accountReader.getAccountByAccountNumber(fromAccountNumber);
            accountValidator.validAccountOwner(fromAccount, loginUser);

            paymentClient.requestTransfer(uuid, fromAccountNumber, toAccountNumber, amount);

            PaymentRecord paymentRecord = new PaymentRecord(uuid, fromAccountNumber, toAccountNumber, amount.negate());
            fromAccount = accountRepository.findByAccountNumberForUpdate(fromAccountNumber)
                    .orElseThrow(() -> new BadRequestException("From account not found"));
            fromAccount.substractBalance(amount);
            paymentRecordRepository.save(paymentRecord);
            CommonUtil.generateRandomException();
        } catch (Exception e) {
            paymentProducer.transferRollback(uuid);
            throw new SsapayException("Cannot transfer money to outer system", e);
        }
    }

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
    public void deleteAccount(AccountDeleteRequest request, LoginUser loginUser) {
        String accountNumber = request.accountNumber();

        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));
        accountValidator.validAccountOwner(account, loginUser);

        account.delete();
    }

    @Transactional(readOnly = true)
    public AllAccountsResponse checkAllAccounts(LoginUser loginUser) {
        List<Account> accounts = accountRepository.findAllAccountByUserId(loginUser.id());
        List<AccountResponse> accountInfos = accounts.stream()
                .map(AccountResponse::from)
                .toList();

        return new AllAccountsResponse(accountInfos);
    }

    @Transactional(readOnly = true)
    public RecordsInPeriodResponse checkPaymentRecordByPeriod(checkPaymentRecordByPeriod request,
                                                              LoginUser loginUser) {
        String accountNumber = request.accountNumber();
        LocalDate startDate = request.startDate();
        LocalDate endDate = request.endDate();

        Account account = accountReader.getAccountByAccountNumber(request.accountNumber());
        accountValidator.validAccountOwner(account, loginUser);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<PaymentRecord> records = accountRepository.findByAccountNumberAndPeriod(accountNumber, start, end);
        List<PaymentRecordResponse> recordInfos = records.stream()
                .map(PaymentRecordResponse::from)
                .toList();

        return new RecordsInPeriodResponse(recordInfos);
    }
}
