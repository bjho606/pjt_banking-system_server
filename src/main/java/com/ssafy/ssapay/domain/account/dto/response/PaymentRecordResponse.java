package com.ssafy.ssapay.domain.account.dto.response;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentRecordResponse {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public PaymentRecordResponse(String fromAccountNumber, String toAccountNumber, BigDecimal amount, LocalDateTime createdAt) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public static PaymentRecordResponse from(PaymentRecord paymentRecord) {
        String fromAccountNumber = (paymentRecord.getFromAccount() != null) ? paymentRecord.getFromAccount().getAccountNumber() : null;

        return new PaymentRecordResponse(
                fromAccountNumber,
                paymentRecord.getToAccount().getAccountNumber(),
                paymentRecord.getAmount(),
                paymentRecord.getCreatedAt());
    }
}
