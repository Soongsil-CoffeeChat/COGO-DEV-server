package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.entity.enums.PartEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserJoinRequest {
        private String phoneNum;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserUpdateRequest {
        private String name;
        private String email;
        private String phoneNum;
        private String role;
        private PartEnum part;
        private ClubEnum club;
        private String picture;
        private Long mentorId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetRequest {
        private String name;
        private String email;
        private String phoneNum;
        private String role;
        private PartEnum part;
        private ClubEnum club;
        private String picture;
    }
}
