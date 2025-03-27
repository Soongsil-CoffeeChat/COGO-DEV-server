package com.soongsil.CoffeeChat.infra.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.infra.admin.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
@Tag(name = "ADMIN", description = "어드민 관련 api")
public class AdminController {
    private final AdminService adminService;

    @GetMapping(value = "/issue")
    @Operation(summary = "어드민 엑세스 토큰 발급")
    public ResponseEntity<ApiResponse<String>> issue(@RequestParam String password) {
        String accessToken = adminService.issueAdmin(password);
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(accessToken));
    }
}
