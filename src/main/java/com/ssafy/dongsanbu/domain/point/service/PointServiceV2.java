package com.ssafy.dongsanbu.domain.point.service;

import com.formssafe.global.aop.Retry;
import com.ssafy.dongsanbu.domain.point.dto.PointInsertDto;
import com.ssafy.dongsanbu.domain.point.mapper.PointMapper;
import com.ssafy.dongsanbu.domain.user.entity.User;
import com.ssafy.dongsanbu.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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
    public void usePoint(int userId, int pointAmount) {
//        int max_retry_count = 10;

//        while (true) {
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
//            userMapper.updatePointWithVersion(map);
        int result = userMapper.updatePointWithVersion(map);
//            System.out.println(result);

        if (result <= 0){
            throw new RuntimeException("포인트 업데이트 fail..");
        }

//        if (result > 0) {
//            break;
//        }
//
//        max_retry_count--;

//            User checkUser = userMapper.findById(userId);
////            System.out.println(checkUser);
//            if (checkUser.getVersion() != originalVersion + 1) {
////                throw new RuntimeException("fail.. 다시 시도합니다");
//                max_retry_count--;
//            } else {
////                System.out.println("성공");
//                break;
//            }

//        if (max_retry_count <= 0) {
//            throw new RuntimeException("포인트 업데이트 fail..");
//        }

//            Thread.sleep(100);
//        }
    }
}
