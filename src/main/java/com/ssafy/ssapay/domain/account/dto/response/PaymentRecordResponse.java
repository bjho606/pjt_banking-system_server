package com.ssafy.ssapay.domain.account.dto.response;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentRecordResponse {
    private Long fromAccountId;
    private long toAccountId;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public PaymentRecordResponse(Long fromAccountId, long toAccountId, BigDecimal amount, LocalDateTime createdAt) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public static PaymentRecordResponse from(PaymentRecord paymentRecord) {
        Long fromAccountId = (paymentRecord.getFromAccount() != null) ? paymentRecord.getFromAccount().getId() : null;

        return new PaymentRecordResponse(
                fromAccountId,
                paymentRecord.getToAccount().getId(),
                paymentRecord.getAmount(),
                paymentRecord.getCreatedAt());
    }
}
