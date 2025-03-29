package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.enums.Role;

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
}
