package com.ssafy.dongsanbu.domain.point.service;

import com.ssafy.dongsanbu.domain.point.dto.PointInsertDto;
import com.ssafy.dongsanbu.domain.point.mapper.PointMapper;
import com.ssafy.dongsanbu.domain.user.entity.User;
import com.ssafy.dongsanbu.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointServiceV3 {
    private final UserMapper userMapper;
    private final PointMapper pointMapper;

    @Transactional
    public void addPoint(int userId, int pointAmount) {
        User user = userMapper.findById(userId);
        if(user == null) {
            throw new RuntimeException("User not found");
        }

        pointMapper.insertPoint(new PointInsertDto(userId, pointAmount));

        user.addPoint(pointAmount);
        userMapper.updatePoint(user);
    }

    @Transactional
    public void usePoint(int userId, int pointAmount) {
        User user = userMapper.findByIdForUpdate(userId);

        if(user == null) {
            throw new RuntimeException("User not found");
        }

        pointMapper.insertPoint(new PointInsertDto(userId, pointAmount));

        user.usePoint(pointAmount);
        userMapper.updatePoint(user);
    }
}
