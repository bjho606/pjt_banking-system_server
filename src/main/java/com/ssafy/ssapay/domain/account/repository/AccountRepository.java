package com.ssafy.ssapay.domain.account.repository;

import com.ssafy.ssapay.domain.account.entity.Account;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("""
            SELECT a FROM Account a WHERE a.id = :id and a.isDeleted = false
            """)
    Optional<Account> findById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a FROM Account a WHERE a.id = :id and a.isDeleted = false
            """)
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT a FROM Account a WHERE a.user.id = :userId and a.isDeleted = false
            """)
    List<Account> findAllAccountByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT r FROM PaymentRecord r WHERE (r.createdAt BETWEEN :start AND :end)
            AND (r.fromAccount.id=:id OR r.toAccount.id=:id) 
            """)
    List<PaymentRecord> findByIdAndPeriod(@Param("id") Long id, @Param("start") LocalDateTime start, @Param("end")LocalDateTime end);

}