package com.ssafy.ssapay.domain.account.dto.request;

import java.math.BigDecimal;

public record TransferRequest(Long fromAccountId,
                              Long toAccountId,
                              BigDecimal amount) {
}
