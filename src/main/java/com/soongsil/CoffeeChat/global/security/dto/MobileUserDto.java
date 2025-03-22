package com.soongsil.CoffeeChat.global.security.dto;

import com.soongsil.CoffeeChat.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MobileUserDto {
    private String role;
    private String name;
    private String username; // 스프링 서버 내의 유저 아이디
    private String email;
    private boolean isNewAccount;

    public User toEntity() {
        return User.builder()
                .role(this.getRole())
                .name(this.getName())
                .username(this.getUsername())
                .email(this.getEmail())
                .build();
    }
}
