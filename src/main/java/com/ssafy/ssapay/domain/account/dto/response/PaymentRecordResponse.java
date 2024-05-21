package com.ssafy.ssapay.domain.account.dto.response;

import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

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
        String fromAccountNumber =
                (paymentRecord.getFromAccountNumber() != null) ? paymentRecord.getFromAccountNumber() : null;

        return new PaymentRecordResponse(
                fromAccountNumber,
                paymentRecord.getToAccountNumber(),
                paymentRecord.getAmount(),
                paymentRecord.getCreatedAt());
    }
}
