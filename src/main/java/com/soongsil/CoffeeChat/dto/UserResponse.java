package com.soongsil.CoffeeChat.dto;

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
        private String role;
        private String phoneNum; // 전화번호
        private String picture;
    }
}
