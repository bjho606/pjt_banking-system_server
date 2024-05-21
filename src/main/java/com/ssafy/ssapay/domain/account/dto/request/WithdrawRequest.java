package com.ssafy.ssapay.domain.account.dto.request;

import java.math.BigDecimal;

public record WithdrawRequest(String accountNumber,
                              BigDecimal amount) {
}
