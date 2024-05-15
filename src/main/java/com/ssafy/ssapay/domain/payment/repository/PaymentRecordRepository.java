package com.ssafy.ssapay.domain.payment.repository;

import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
}