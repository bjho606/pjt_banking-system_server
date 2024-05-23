package com.ssafy.ssapay.infra.repository;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a FROM Account a WHERE a.accountNumber = :accountNumber and a.isDeleted = false
            """)
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
            FROM Account a
            WHERE a.accountNumber = :accountNumber 
            and a.isDeleted = false
            """)
    boolean existsByAccountNumber(String accountNumber);


    @Query("""
            SELECT a FROM Account a WHERE a.accountNumber = :accountNumber and a.isDeleted = false
            """)
    Optional<Account> findByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("""
            SELECT a FROM Account a WHERE a.user.id = :userId and a.isDeleted = false
            """)
    List<Account> findAllAccountByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT r FROM PaymentRecord r WHERE (r.createdAt BETWEEN :start AND :end)
            AND (r.fromAccountNumber=:accountNumber OR r.toAccountNumber=:accountNumber) 
            """)
    List<PaymentRecord> findByAccountNumberAndPeriod(@Param("accountNumber") String accountNumber,
                                                     @Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);
}