package com.ssafy.ssapay.domain.account.implement;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.global.error.type.BadRequestException;
import com.ssafy.ssapay.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class AccountReader {
    private final AccountRepository accountRepository;

    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account not found"));
    }
}
