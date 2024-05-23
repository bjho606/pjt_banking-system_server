package com.ssafy.ssapay.infra.repository;

import com.ssafy.ssapay.domain.user.entity.UserSecret;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSecretRepository extends JpaRepository<UserSecret, String> {

}