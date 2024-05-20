package com.ssafy.ssapay.infra.repository.read;

import com.ssafy.ssapay.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface UserReadRepository extends JpaRepository<User, Long> {
}