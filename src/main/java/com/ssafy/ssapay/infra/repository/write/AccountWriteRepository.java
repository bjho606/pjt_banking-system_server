package com.ssafy.ssapay.infra.repository.write;

import com.ssafy.ssapay.domain.account.entity.Account;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountWriteRepository extends JpaRepository<Account, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a FROM Account a WHERE a.accountNumber = :accountNumber and a.isDeleted = false
            """)
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

}