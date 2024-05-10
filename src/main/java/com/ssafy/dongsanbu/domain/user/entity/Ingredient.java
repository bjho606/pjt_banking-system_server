package com.ssafy.dongsanbu.domain.user.entity;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Ingredient implements Serializable {
    private String id;
    private String salt;

    @Builder
    public Ingredient(String id,
                      String salt) {
        this.id = id;
        this.salt = salt;
    }
}