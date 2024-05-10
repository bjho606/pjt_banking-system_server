package com.ssafy.dongsanbu.domain.user.service;

import com.ssafy.dongsanbu.domain.user.dto.UserCreateRequest;
import com.ssafy.dongsanbu.domain.user.dto.UserProfileImageUpdateDto;
import com.ssafy.dongsanbu.domain.user.dto.UserResponse;
import com.ssafy.dongsanbu.domain.user.dto.UserUpdateRequest;
import com.ssafy.dongsanbu.domain.user.entity.Ingredient;
import com.ssafy.dongsanbu.domain.user.entity.User;
import com.ssafy.dongsanbu.domain.user.mapper.UserMapper;
import com.ssafy.dongsanbu.global.error.type.BadRequestException;
import com.ssafy.dongsanbu.global.error.type.DataNotFoundException;
import com.ssafy.dongsanbu.global.util.MyCrypt;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    @Value("${image.upload-locations}")
    private String uploadLocations;

    public void createUser(UserCreateRequest request) {
        String userSalt = MyCrypt.makeSalt();
        String encodedPassword = MyCrypt.byteArrayToHex(MyCrypt.getSHA256(request.password(), userSalt));
        User user = request.toUserEntity(encodedPassword);

        userMapper.registUser(user);
        userMapper.saveIngredient(Ingredient.builder()
                .id(request.username())
                .salt(userSalt)
                .build());
    }

    public void updateUser(int userId, UserUpdateRequest request) {
        User user = request.toUserEntity(userId);

        userMapper.updateUser(user);
    }

    public void deleteUser(int userId) {
        userMapper.deleteUser(userId);
    }

    public UserResponse getUser(int userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("사용자를 찾을 수 없습니다.");
        }

        return UserResponse.from(user);
    }

    public void updateProfileImage(int id, MultipartFile profileImage) {
        if (profileImage == null) {
            throw new BadRequestException("파일이 없습니다.");
        }

        File uploadDir = new File(uploadLocations);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String uploadPath = uploadLocations + System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
        File profileImageFile = new File(uploadPath);
        try {
            profileImage.transferTo(profileImageFile);
        } catch (IOException e) {
            throw new BadRequestException("프로필 사진을 저장할 수 없습니다.", e);
        }

        userMapper.updateProfileImage(new UserProfileImageUpdateDto(id, uploadPath));
    }
}
