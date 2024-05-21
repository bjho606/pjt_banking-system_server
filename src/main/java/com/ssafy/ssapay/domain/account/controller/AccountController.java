package com.ssafy.ssapay.domain.account.controller;

import com.ssafy.ssapay.domain.account.dto.request.AccountCreateRequest;
import com.ssafy.ssapay.domain.account.dto.request.AccountDeleteRequest;
import com.ssafy.ssapay.domain.account.dto.request.CheckAllAccountsRequest;
import com.ssafy.ssapay.domain.account.dto.request.CheckBalanceRequest;
import com.ssafy.ssapay.domain.account.dto.request.DepositRequest;
import com.ssafy.ssapay.domain.account.dto.request.TransferRequest;
import com.ssafy.ssapay.domain.account.dto.request.WithdrawRequest;
import com.ssafy.ssapay.domain.account.dto.request.checkPaymentRecordByPeriod;
import com.ssafy.ssapay.domain.account.dto.response.AllAccountsResponse;
import com.ssafy.ssapay.domain.account.dto.response.BalanceResponse;
import com.ssafy.ssapay.domain.account.dto.response.RecordsInPeriodResponse;
import com.ssafy.ssapay.domain.account.dto.response.accountNumberResponse;
import com.ssafy.ssapay.domain.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public accountNumberResponse createAccount(@RequestBody AccountCreateRequest request) {
        return accountService.createAccount(request.userId());
    }

    @GetMapping("/balance")
    public BalanceResponse checkBalance(@RequestBody CheckBalanceRequest request) {
        return accountService.checkBalance(request.accountNumber());
    }

    @PostMapping("/deposit")
    public void deposit(@RequestBody DepositRequest request) {
        accountService.deposit(request.accountNumber(), request.amount());
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody WithdrawRequest request) {
        accountService.withdraw(request.accountNumber(), request.amount());
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferRequest request) {
        accountService.transfer(request.fromAccountNumber(), request.toAccountNumber(), request.amount());
    }

    @PostMapping("/transfer/external")
    public void transferExternal(@RequestBody TransferRequest request) {
        accountService.transferExternal(request.fromAccountNumber(), request.toAccountNumber(), request.amount());
    }

    @DeleteMapping
    public void deleteAccount(@RequestBody AccountDeleteRequest request) {
        accountService.deleteAccount(request.accountNumber());
    }

    @GetMapping("/accountInfos")
    public AllAccountsResponse checkAllAccounts(@RequestBody CheckAllAccountsRequest request) {
        return accountService.checkAllAccounts(request.userId());
    }

    @GetMapping("/recordByPeriod")
    public RecordsInPeriodResponse checkPaymentRecordByPeriod(@RequestBody checkPaymentRecordByPeriod request) {
        return accountService.checkPaymentRecordByPeriod(request.accountNumber(), request.startDate(), request.endDate());
    }

}
