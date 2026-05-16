package com.soongsil.CoffeeChat.domain.assignedcoupon.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.assignedcoupon.dto.*;
import com.soongsil.CoffeeChat.domain.assignedcoupon.service.AssignedCouponService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/assigned-coupons")
@Tag(name = "ASSIGNED COUPON", description = "축제 전용 지정 커피 쿠폰 관련 API")
public class AssignedCouponController {

    private final AssignedCouponService assignedCouponService;

    // 유저 - 보관함 진입 시 발급 자격 확인
    @GetMapping("/eligibility")
    @Operation(
            summary = "지정 쿠폰 발급 자격 확인",
            description =
                    "보관함 진입 시 호출합니다. 본인의 name + phoneNum 이 사전 등록된 대상자 명단과 일치하는지 확인합니다."
                            + " 발급 이력이 있는 경우 발급 정보를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공 (result.eligible=true && !alreadyIssued 일 때만 발급 버튼 활성화)")
    public ResponseEntity<ApiResponse<AssignedCouponCheckResponse>> checkEligibility(
            Authentication authentication) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                assignedCouponService.checkEligibility(
                                        authentication.getName())));
    }

    // 유저 - 매장 PIN 인증으로 지정 쿠폰 발급 (=사용 처리)
    @PostMapping("/coupons")
    @Operation(
            summary = "지정 쿠폰 발급 (매장 PIN 인증)",
            description = "매장 직원의 핀 번호를 입력받아 최종 검증 후 쿠폰을 발급합니다. 발급과 동시에 사용 처리되어 재사용 불가합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "발급된 쿠폰 정보 반환")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "EVENT_400_1: 핀 번호 불일치 | ASSIGNED_COUPON_400: 전화번호 미등록",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "ASSIGNED_COUPON_404: 발급 대상자 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "ASSIGNED_COUPON_409: 이미 발급된 쿠폰",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503",
            description = "EVENT_503: 동시성 처리 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ApiResponse<AssignedCouponResponse>> issueCoupon(
            @Valid @RequestBody AssignedCouponIssueRequest request,
            Authentication authentication) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                assignedCouponService.issueCoupon(
                                        authentication.getName(), request.storePin())));
    }

    // ************ 관리자용 api ************

    @PostMapping("/admin/register")
    @Operation(
            summary = "지정 쿠폰 대상자 일괄 등록 (관리자)",
            description = "대상자(이름 + 전화번호) 목록을 받아 Redis에 등록합니다. 전화번호는 하이픈 유무 무관.")
    public ResponseEntity<ApiResponse<AssignedCouponRegisterResult>> registerTargets(
            @RequestBody List<AssignedCouponTargetRequest> targets) {
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccessOK(assignedCouponService.registerTargets(targets)));
    }

    @PostMapping("/admin/register/one")
    @Operation(summary = "지정 쿠폰 대상자 단건 등록 (관리자)")
    public ResponseEntity<ApiResponse<AssignedCouponRegisterResult>> registerOneTarget(
            @Valid @RequestBody AssignedCouponTargetRequest target) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                assignedCouponService.registerTargets(List.of(target))));
    }
}