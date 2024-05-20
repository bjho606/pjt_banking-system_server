package com.ssafy.ssapay.infra.repository.read;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.payment.entity.PaymentRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountReadRepository extends JpaRepository<Account, Long> {

    @Query("""
            SELECT a FROM Account a WHERE a.id = :id and a.isDeleted = false
            """)
    Optional<Account> findById(@Param("id") Long id);

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