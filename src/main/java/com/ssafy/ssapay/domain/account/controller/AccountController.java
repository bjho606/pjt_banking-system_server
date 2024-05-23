package com.ssafy.ssapay.domain.account.controller;

import com.ssafy.ssapay.domain.account.dto.request.AccountDeleteRequest;
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
import com.ssafy.ssapay.domain.auth.dto.internal.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public accountNumberResponse createAccount(@AuthenticationPrincipal LoginUser loginUser) {
        return accountService.createAccount(loginUser);
    }

    @GetMapping("/balance")
    public BalanceResponse checkBalance(@RequestBody CheckBalanceRequest request,
                                        @AuthenticationPrincipal LoginUser loginUser) {
        return accountService.checkBalance(request, loginUser);
    }

    @PostMapping("/deposit")
    public void deposit(@RequestBody DepositRequest request,
                        @AuthenticationPrincipal LoginUser loginUser) {
        accountService.deposit(request, loginUser);
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody WithdrawRequest request,
                         @AuthenticationPrincipal LoginUser loginUser) {
        accountService.withdraw(request, loginUser);
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferRequest request,
                         @AuthenticationPrincipal LoginUser loginUser) {
        accountService.transfer(request, loginUser);
    }

    @PostMapping("/transfer/external")
    public void transferExternal(@RequestBody TransferRequest request) {
        accountService.transferExternal(request.uuid(),
                request.fromAccountNumber(),
                request.toAccountNumber(),
                request.amount());
    }

    @DeleteMapping
    public void deleteAccount(@RequestBody AccountDeleteRequest request,
                              @AuthenticationPrincipal LoginUser loginUser) {
        accountService.deleteAccount(request, loginUser);
    }

    @GetMapping("/accountInfos")
    public AllAccountsResponse checkAllAccounts(@AuthenticationPrincipal LoginUser loginUser) {
        return accountService.checkAllAccounts(loginUser);
    }

    @GetMapping("/recordByPeriod")
    public RecordsInPeriodResponse checkPaymentRecordByPeriod(@RequestBody checkPaymentRecordByPeriod request,
                                                              @AuthenticationPrincipal LoginUser loginUser) {
        return accountService.checkPaymentRecordByPeriod(request, loginUser);
    }
}
