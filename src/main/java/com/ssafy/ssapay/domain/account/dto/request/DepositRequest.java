package com.ssafy.ssapay.domain.account.dto.request;

import java.math.BigDecimal;

public record DepositRequest(String accountNumber,
                             BigDecimal amount) {
}
