package com.ssafy.ssapay.domain.user.service;

import com.ssafy.ssapay.domain.user.dto.internal.UserCreateDto;
import com.ssafy.ssapay.domain.user.dto.request.UserCreateRequest;
import com.ssafy.ssapay.domain.user.dto.response.UserResponse;
import com.ssafy.ssapay.domain.user.implementation.UserReader;
import com.ssafy.ssapay.domain.user.implementation.UserValidator;
import com.ssafy.ssapay.domain.user.implementation.UserWriter;
import com.ssafy.ssapay.global.util.MyCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserWriter userWriter;
    private final UserReader userReader;
    private final UserValidator userValidator;

    public void createUser(UserCreateRequest request) {
        String username = request.username();
        boolean isExists = userReader.isExistUser(username);
        userValidator.exists(isExists);

        String salt = MyCrypt.makeSalt();
        String encodedPassword = MyCrypt.byteArrayToHex(MyCrypt.getSHA256(request.password(), salt));

        UserCreateDto userCreateDto = new UserCreateDto(request.username(),
                encodedPassword,
                request.email(),
                salt);
        userWriter.appendNewUser(userCreateDto);
    }

    public UserResponse getUserProfile(Long id) {
        return UserResponse.from(userReader.getUserById(id));
    }
}
