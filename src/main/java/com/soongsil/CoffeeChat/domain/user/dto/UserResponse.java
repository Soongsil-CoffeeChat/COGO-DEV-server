package com.soongsil.CoffeeChat.domain.user.dto;

import com.soongsil.CoffeeChat.domain.auth.enums.Role;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;

import lombok.*;

public class UserResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserInfoResponse {
        private String username;
        private String name;
        private String email;
        private Role role;
        private String phoneNum; // 전화번호
        private String picture;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class User2FACodeResponse {
        private String verificationCode;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetResponse {
        private Long userId;
        private String name;
        private String email;
        private String phoneNum;
        private Role role;
        private PartEnum part;
        private ClubEnum club;
        private String picture;
    }
}
