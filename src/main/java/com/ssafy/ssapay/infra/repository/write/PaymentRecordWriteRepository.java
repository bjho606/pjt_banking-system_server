package com.ssafy.ssapay.infra.repository.write;

import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordWriteRepository extends JpaRepository<PaymentRecord, Long> {
}