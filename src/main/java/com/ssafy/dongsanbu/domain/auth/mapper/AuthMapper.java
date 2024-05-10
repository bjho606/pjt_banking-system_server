package com.ssafy.dongsanbu.domain.auth.mapper;

import com.ssafy.dongsanbu.domain.auth.dto.LoginDto;
import com.ssafy.dongsanbu.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {

    User findByUsernameAndPassword(LoginDto loginDto);
}
