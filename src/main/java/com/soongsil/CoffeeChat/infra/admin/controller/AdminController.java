package com.soongsil.CoffeeChat.infra.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.user.dto.UserResponse.UserInfoResponse;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.infra.admin.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v2/admin")
@Tag(name = "ADMIN", description = "어드민 관련 api")
public class AdminController {
    private final AdminService adminService;

    @PostMapping(value = "/users/issue")
    @Operation(summary = "ROLE_ADMIN 권한을 보유한 유저 엑세스 토큰 발급")
    public ResponseEntity<ApiResponse<String>> issue(
            @RequestParam String username, @RequestParam String password) {
        String accessToken = adminService.issueAdmin(username, password);
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(accessToken));
    }

    @GetMapping(value = "/users/list")
    @Operation(summary = "유저 리스트 조회")
    public ResponseEntity<ApiResponse<List<UserInfoResponse>>> getUserList(
            @RequestParam String password) {
        List<UserInfoResponse> responses = adminService.getUserInfoList(password);
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(responses));
    }
}
