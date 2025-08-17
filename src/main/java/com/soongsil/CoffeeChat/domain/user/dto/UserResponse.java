package com.soongsil.CoffeeChat.domain.user.dto;

import com.soongsil.CoffeeChat.domain.auth.enums.Role;
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
}
