package com.ssafy.dongsanbu.domain.token.mapper;

import com.ssafy.dongsanbu.domain.token.dto.Token;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogoutTokenMapper {

    boolean exists(Token token);

    void save(Token token, long expireTime);

    void delete(Token token);
}
