package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.User;
import com.soongsil.CoffeeChat.domain.entity.enums.Role;
import com.soongsil.CoffeeChat.global.security.dto.GoogleTokenInfoResponse;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.OAuth2Response;

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

    public static UserResponse.User2FACodeResponse to2FACodeResponse(String code) {
        return UserResponse.User2FACodeResponse.builder().verificationCode(code).build();
    }

    public static User toEntity(String username, OAuth2Response oAuth2Response) {
        return User.builder()
                .username(username)
                .email(oAuth2Response.getEmail())
                .name(oAuth2Response.getName())
                .role(Role.ROLE_USER)
                .build();
    }

    public static User toEntity(String username, GoogleTokenInfoResponse response) {
        return User.builder()
                .username(username)
                .email(response.getEmail())
                .name(response.getName())
                .role(Role.ROLE_USER)
                .build();
        // String email = tokenInfo.getEmail();
        //            String username = tokenInfo.getSub();
        //            String userName = tokenInfo.getName();
    }
}
