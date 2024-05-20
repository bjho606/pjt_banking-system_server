package com.ssafy.ssapay.domain.account.dto.response;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;

import java.util.List;

public record RecordsInPeriodResponse(List<PaymentRecordResponse> records) {
}
