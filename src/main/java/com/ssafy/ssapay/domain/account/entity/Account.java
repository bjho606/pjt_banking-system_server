package com.ssafy.ssapay.domain.account.entity;

import com.ssafy.ssapay.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class Account {

    @Id
    @Column(nullable = false, unique = true)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @ColumnDefault("0")
    private BigDecimal balance = BigDecimal.valueOf(0);

    @Column
    @ColumnDefault("0")
    private boolean isDeleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Account(User user, String accountNumber) {
        this.user = user;
        this.accountNumber = accountNumber;
    }

    public Account(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void substractBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public boolean isLess(BigDecimal amount) {
        return balance.compareTo(amount) < 0;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
