package com.ssafy.ssapay.infra.repository;

import com.ssafy.ssapay.domain.user.entity.OauthId;
import com.ssafy.ssapay.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByUsername(String username);

    Optional<User> findByOauthId(OauthId oauthId);
}