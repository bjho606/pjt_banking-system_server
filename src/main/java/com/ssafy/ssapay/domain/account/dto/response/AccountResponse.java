package com.ssafy.ssapay.domain.account.dto.response;

import com.ssafy.ssapay.domain.account.entity.Account;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AccountResponse {
    private String accountNumber;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public AccountResponse(String accountNumber, BigDecimal balance, LocalDateTime createdAt) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountNumber(),
                account.getBalance(),
                account.getCreatedAt());
    }
}
