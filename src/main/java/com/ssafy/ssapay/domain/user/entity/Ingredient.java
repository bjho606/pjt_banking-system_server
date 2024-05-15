package com.ssafy.ssapay.domain.user.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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