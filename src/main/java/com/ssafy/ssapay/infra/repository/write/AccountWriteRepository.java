package com.ssafy.ssapay.infra.repository.write;

import com.ssafy.ssapay.domain.account.entity.Account;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountWriteRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a FROM Account a WHERE a.id = :id and a.isDeleted = false
            """)
    Optional<Account> findByIdForUpdate(@Param("id") Long id);
}