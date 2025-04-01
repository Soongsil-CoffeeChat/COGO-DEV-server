package com.soongsil.CoffeeChat.global.security.dto;

import com.soongsil.CoffeeChat.domain.entity.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Role role;
    private String name;
    private String username; // 스프링 서버 내의 유저 아이디
}
