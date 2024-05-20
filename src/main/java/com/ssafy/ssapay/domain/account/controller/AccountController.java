package com.ssafy.ssapay.domain.account.controller;

import com.ssafy.ssapay.domain.account.dto.request.*;
import com.ssafy.ssapay.domain.account.dto.response.AccountIdResponse;
import com.ssafy.ssapay.domain.account.dto.response.BalanceResponse;
import com.ssafy.ssapay.domain.account.service.AccountService;
import com.ssafy.ssapay.domain.account.dto.request.CheckAllAccountsRequest;
import com.ssafy.ssapay.domain.account.dto.response.AllAccountsResponse;
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
    public AccountIdResponse createAccount(@RequestBody AccountCreateRequest request) {
        return accountService.createAccount(request.userId());
    }

    @GetMapping("/balance")
    public BalanceResponse checkBalance(@RequestBody CheckBalanceRequest request) {
        return accountService.checkBalance(request.accountId());
    }

    @PostMapping("/deposit")
    public void deposit(@RequestBody DepositRequest request) {
        accountService.deposit(request.accountId(), request.amount());
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody WithdrawRequest request) {
        accountService.withdraw(request.accountId(), request.amount());
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferRequest request) {
        accountService.transfer(request.fromAccountId(), request.toAccountId(), request.amount());
    }

    @DeleteMapping
    public void deleteAccount(@RequestBody AccountDeleteRequest request) {
        accountService.deleteAccount(request.accountId());
    }

    @GetMapping("/accountInfos")
    public AllAccountsResponse checkAllAccounts(@RequestBody CheckAllAccountsRequest request) {
        return accountService.checkAllAccounts(request.userId());
    }

}
