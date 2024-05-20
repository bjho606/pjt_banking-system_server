package com.ssafy.ssapay.domain.account.dto.response;

import com.ssafy.ssapay.domain.account.entity.Account;

import java.util.List;

public record AllAccountsResponse(List<AccountResponse> accountInfo) {

    private AccountResponse from(Account account) {
        return new AccountResponse(
                account.getAccountNumber(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}
