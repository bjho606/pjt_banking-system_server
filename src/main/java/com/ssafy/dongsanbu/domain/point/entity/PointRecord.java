package com.ssafy.dongsanbu.domain.point.entity;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PointRecord {
    private int id;

    private int member;

    private int point;

    private LocalDateTime createdAt;
}