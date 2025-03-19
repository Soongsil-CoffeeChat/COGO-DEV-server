package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.dto.UserResponse.*;
import com.soongsil.CoffeeChat.entity.User;

public class UserConverter {
    public static UserInfoResponse toResponse(User user) {
        return UserInfoResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNum(user.getPhoneNum())
                .picture(user.getPicture())
                .build();
    }
}
