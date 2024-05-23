package com.ssafy.ssapay.domain.user.implementation;

import com.ssafy.ssapay.global.error.type.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserValidator {

    public void exists(boolean isExists) {
        if (isExists) {
            throw new BadRequestException("이미 존재하는 사용자입니다.");
        }
    }
}
