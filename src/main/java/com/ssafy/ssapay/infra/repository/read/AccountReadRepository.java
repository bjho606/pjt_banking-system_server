package com.ssafy.ssapay.infra.repository.read;

import com.ssafy.ssapay.domain.account.entity.Account;
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
}