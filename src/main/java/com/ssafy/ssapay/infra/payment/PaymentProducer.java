package com.ssafy.ssapay.infra.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {
    private final KafkaTemplate kafkaTemplate;

    public void transferRollback(String uuid) {
        kafkaTemplate.send("transfer-rollback", uuid);
    }
}
