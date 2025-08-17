package com.soongsil.CoffeeChat.domain.user.dto;

import com.soongsil.CoffeeChat.domain.auth.enums.Role;
import com.soongsil.CoffeeChat.domain.mentor.enums.ClubEnum;
import com.soongsil.CoffeeChat.domain.mentor.enums.PartEnum;
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
