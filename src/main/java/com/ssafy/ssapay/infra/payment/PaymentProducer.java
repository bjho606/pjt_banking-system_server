package com.ssafy.ssapay.infra.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {
    private final KafkaTemplate kafkaTemplate;

    public void transferRollback(String uuid) {
        log.info("======[Send Transfer Rollback] {}======", uuid);
        kafkaTemplate.send("transfer-rollback", uuid);
    }
}
