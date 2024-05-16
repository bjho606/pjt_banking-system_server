package com.ssafy.ssapay.domain.account.dto;

import java.math.BigDecimal;

public class TransferDto {
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
}
