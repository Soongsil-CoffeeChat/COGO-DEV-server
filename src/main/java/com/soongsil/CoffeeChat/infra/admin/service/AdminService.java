package com.soongsil.CoffeeChat.infra.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.domain.dto.UserConverter;
import com.soongsil.CoffeeChat.domain.dto.UserResponse.UserInfoResponse;
import com.soongsil.CoffeeChat.domain.entity.enums.Role;
import com.soongsil.CoffeeChat.domain.repository.User.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${admin.password}")
    private String adminPassword;

    public String issueAdmin(String username, String password) {
        if (!password.equals(adminPassword)) {
            throw new GlobalException(GlobalErrorCode.ADMIN_INCORRECT_PASSWORD);
        }
        userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
        return jwtUtil.createAccessToken(username, Role.ROLE_ADMIN);
    }

    public List<UserInfoResponse> getUserInfoList(String password) {
        if (!password.equals(adminPassword)) {
            throw new GlobalException(GlobalErrorCode.ADMIN_INCORRECT_PASSWORD);
        }
        return userRepository.findAll().stream().map(UserConverter::toResponse).toList();
    }
}
