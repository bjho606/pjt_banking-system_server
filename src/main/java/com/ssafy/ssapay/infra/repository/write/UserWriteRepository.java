package com.ssafy.ssapay.infra.repository.write;

import com.ssafy.ssapay.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWriteRepository extends JpaRepository<User, Long> {
}