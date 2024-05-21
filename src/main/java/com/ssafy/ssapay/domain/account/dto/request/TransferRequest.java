package com.ssafy.ssapay.domain.account.dto.request;

import java.math.BigDecimal;

public record TransferRequest(String fromAccountNumber,
                              String toAccountNumber,
                              BigDecimal amount) {
}
