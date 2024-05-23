package com.ssafy.ssapay.infra.payment;

import com.ssafy.ssapay.domain.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRollbackConsumer {
    private final AccountService accountService;

    @KafkaListener(topics = "transfer-rollback", groupId = "group-01")
    public void rollback(String uuid) {
        log.error("======[Rollback] {}======", uuid);
        accountService.rollbackTransfer(uuid);
    }
}
