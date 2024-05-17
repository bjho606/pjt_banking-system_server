package com.ssafy.ssapay.domain.account.repository;

import com.ssafy.ssapay.domain.account.entity.Account;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a FROM Account a WHERE a.id = :id and a.isDeleted = false
            """)
    Optional<Account> findById(@Param("id") Long id);

    @Query("""
            SELECT a FROM Account a WHERE a.id = :id and a.isDeleted = false
            """)
    Optional<Account> findByIdForUpdate(@Param("id") Long id);
}