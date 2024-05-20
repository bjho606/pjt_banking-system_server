package com.ssafy.ssapay.domain.user.repository;

import com.ssafy.ssapay.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}