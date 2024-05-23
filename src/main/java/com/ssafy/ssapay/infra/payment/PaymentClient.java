package com.ssafy.ssapay.infra.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class PaymentClient {
    private final WebClient webClient = WebClient.create();
    @Value("${external.transfer.url}")
    private String externalTransferUrl;

    public void requestTransfer(String uuid, String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        log.debug("transfer {} to {} {}", fromAccountNumber, toAccountNumber, amount);

        Map<String, Object> body = new HashMap<>();
        body.put("uuid", uuid);
        body.put("fromAccountNumber", fromAccountNumber);
        body.put("toAccountNumber", toAccountNumber);
        body.put("amount", amount);

        String response = webClient.post()
                .uri(externalTransferUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Log the response
        log.debug("Response from external API: {}", response);
    }
}
