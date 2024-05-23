package com.ssafy.ssapay.infra.repository;

import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord> findByUuid(String uuid);
}