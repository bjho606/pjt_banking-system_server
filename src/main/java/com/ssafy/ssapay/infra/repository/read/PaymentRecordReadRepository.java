package com.ssafy.ssapay.infra.repository.read;

import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface PaymentRecordReadRepository extends JpaRepository<PaymentRecord, Long> {
}