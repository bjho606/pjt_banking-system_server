package com.ssafy.ssapay.domain.account.dto.request;

import java.math.BigDecimal;

public record WithdrawRequest(Long accountId,
                              BigDecimal amount) {
}
