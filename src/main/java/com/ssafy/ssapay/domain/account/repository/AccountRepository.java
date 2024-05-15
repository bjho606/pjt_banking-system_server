package com.ssafy.ssapay.domain.account.repository;

import com.ssafy.ssapay.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}