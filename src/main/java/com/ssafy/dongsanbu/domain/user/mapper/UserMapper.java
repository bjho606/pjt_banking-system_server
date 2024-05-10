package com.ssafy.dongsanbu.domain.user.mapper;

import com.ssafy.dongsanbu.domain.user.dto.UserProfileImageUpdateDto;
import com.ssafy.dongsanbu.domain.user.entity.Ingredient;
import com.ssafy.dongsanbu.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User findById(int id);

    Ingredient findIngredientById(String id);

    void registUser(User user);

    void saveIngredient(Ingredient ingredient);

    void updateUser(User user);

    void updateProfileImage(UserProfileImageUpdateDto dto);

    void deleteUser(int id);
}
