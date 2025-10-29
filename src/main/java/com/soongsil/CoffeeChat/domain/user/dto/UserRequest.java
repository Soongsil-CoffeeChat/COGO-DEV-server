package com.soongsil.CoffeeChat.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserUpdateRequest {
        private String name;
        private String email;
        private String phoneNum;
    }
}
