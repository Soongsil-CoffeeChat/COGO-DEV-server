package com.soongsil.CoffeeChat.infra.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.domain.entity.enums.Role;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final JwtUtil jwtUtil;

    @Value("${admin.password}")
    private String adminPassword;

    public String issueAdmin(String password) {
        if (!password.equals(adminPassword)) {
            throw new GlobalException(GlobalErrorCode.ADMIN_INCORRECT_PASSWORD);
        }
        String accessToken = jwtUtil.createAccessToken("어드민", Role.ROLE_ADMIN);
        return accessToken;
    }
}
