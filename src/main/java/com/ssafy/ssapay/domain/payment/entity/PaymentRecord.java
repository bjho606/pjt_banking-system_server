package com.ssafy.ssapay.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name="payment_record")
@NoArgsConstructor
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    private String fromAccountNumber;

    private String toAccountNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PaymentRecord(String uuid, String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        this.uuid = uuid;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public PaymentRecord(String toAccountNumber, BigDecimal amount) {
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    public PaymentRecord(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        uuid = uuid == null ? UUID.randomUUID().toString() : uuid;
    }
}
