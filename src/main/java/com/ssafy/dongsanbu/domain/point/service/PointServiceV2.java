package com.ssafy.dongsanbu.domain.point.service;

import com.ssafy.dongsanbu.domain.point.dto.PointInsertDto;
import com.ssafy.dongsanbu.domain.point.mapper.PointMapper;
import com.ssafy.dongsanbu.domain.user.entity.User;
import com.ssafy.dongsanbu.domain.user.mapper.UserMapper;
import com.ssafy.dongsanbu.global.aop.Retry;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointServiceV2 {
    private final UserMapper userMapper;
    private final PointMapper pointMapper;

    @Transactional
    public void addPoint(int userId, int pointAmount) {
        pointMapper.insertPoint(new PointInsertDto(userId, pointAmount));
        User user = userMapper.findById(userId);
        if(user == null) {
            throw new RuntimeException("User not found");
        }

        user.addPoint(pointAmount);
    }

    @Transactional
    @Retry
    public void usePoint(int userId, int pointAmount) {
        User user = userMapper.findById(userId);
        if(user == null) {
            throw new RuntimeException("User not found");
        }

        pointMapper.insertPointWithVersion(new PointInsertDto(userId, pointAmount));
        user.usePoint(pointAmount);
        int originalVersion = user.getVersion();
        Map<String, Object> map = new HashMap<>();
        map.put("point", user.getPoint());
        map.put("id", user.getId());
        map.put("originalVersion", originalVersion);

        int result = userMapper.updatePointWithVersion(map);

        if (result <= 0){
            throw new IllegalStateException("포인트 업데이트 fail..");
        }
    }
}
