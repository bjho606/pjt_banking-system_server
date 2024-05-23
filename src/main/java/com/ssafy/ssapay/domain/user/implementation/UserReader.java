package com.ssafy.ssapay.domain.user.implementation;

import com.ssafy.ssapay.domain.auth.dto.request.LoginRequest;
import com.ssafy.ssapay.domain.user.entity.User;
import com.ssafy.ssapay.domain.user.entity.UserSecret;
import com.ssafy.ssapay.global.error.type.BadRequestException;
import com.ssafy.ssapay.global.util.MyCrypt;
import com.ssafy.ssapay.infra.repository.UserRepository;
import com.ssafy.ssapay.infra.repository.UserSecretRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class UserReader {
    private final UserRepository userRepository;
    private final UserSecretRepository userSecretRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 사용자입니다."));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 사용자입니다."));
    }

    public boolean isExistUser(String username) {
        return userRepository.existsByUsername(username);
    }

    public User getUserByUsernameAndPassword(LoginRequest request) {
        UserSecret userSecret = getUserSecret(request.username());
        String encodedPassword = MyCrypt.byteArrayToHex(
                MyCrypt.getSHA256(request.password(), userSecret.getSalt()));

        return userRepository.findByUsernameAndPassword(request.username(), encodedPassword)
                .orElseThrow(() -> new BadRequestException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    private UserSecret getUserSecret(String username) {
        return userSecretRepository.findById(username)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 사용자입니다."));
    }
}
