package com.ssafy.ssapay.domain.account.implement;

import com.ssafy.ssapay.domain.account.entity.Account;
import com.ssafy.ssapay.domain.auth.dto.internal.LoginUser;
import com.ssafy.ssapay.global.error.type.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

    public void validAccountOwner(Account account, LoginUser loginUser) {
        if (!account.getUser().getId().equals(loginUser.id())) {
            throw new BadRequestException("계좌 소유자가 아닙니다.");
        }
    }
}
