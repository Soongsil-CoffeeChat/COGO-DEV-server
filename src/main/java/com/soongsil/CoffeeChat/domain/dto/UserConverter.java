package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.User;

public class UserConverter {
    public static UserResponse.UserInfoResponse toResponse(User user) {
        return UserResponse.UserInfoResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNum(user.getPhoneNum())
                .picture(user.getPicture())
                .build();
    }
}
