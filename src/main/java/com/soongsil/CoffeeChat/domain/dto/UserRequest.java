package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.entity.enums.PartEnum;
import com.soongsil.CoffeeChat.domain.entity.enums.Role;

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
        private String picture;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetRequest {
        private String name;
        private String email;
        private String phoneNum;
        private Role role;
        private PartEnum part;
        private ClubEnum club;
        private String picture;
    }
}
