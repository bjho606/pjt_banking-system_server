package com.ssafy.dongsanbu.domain.point.mapper;

import com.ssafy.dongsanbu.domain.point.dto.PointInsertDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointMapper {

    void insertPoint(PointInsertDto pointInsertDto);
}
