package com.ssafy.ssapay.domain.user.implementation;

import com.ssafy.ssapay.domain.user.dto.internal.UserCreateDto;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.entity.UserSecret;
import com.ssafy.ssapay.infra.repository.UserRepository;
import com.ssafy.ssapay.infra.repository.UserSecretRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class UserWriter {
    private final UserRepository userRepository;
    private final UserSecretRepository userSecretRepository;

    public void appendNewUser(UserCreateDto dto) {
        User user = dto.toUserEntity();
        UserSecret userSecret = dto.toUserSecretEntity();

        userRepository.save(user);
        userSecretRepository.save(userSecret);
    }
}
