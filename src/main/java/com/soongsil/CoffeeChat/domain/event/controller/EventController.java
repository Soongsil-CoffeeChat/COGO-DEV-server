package com.soongsil.CoffeeChat.domain.event.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.event.dto.EventCheckResponse;
import com.soongsil.CoffeeChat.domain.event.dto.EventStatusResponse;
import com.soongsil.CoffeeChat.domain.event.service.CouponService;
import com.soongsil.CoffeeChat.domain.event.service.CouponSetupService;
import com.soongsil.CoffeeChat.global.api.ApiResponse;
import com.soongsil.CoffeeChat.global.util.QrCodeUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/events")
@Tag(name = "EVENT", description = "커피챗 이벤트 쿠폰 관련 API")
public class EventController {

    private final CouponService couponService;
    private final CouponSetupService couponSetupService;

    // 쿠폰 발급 자격 확인
    @GetMapping("/check-eligibility")
    @Operation(
            summary = "쿠폰 발급 자격 확인",
            description = "해당 채팅방의 발급 이력 및 멘티의 잔여 횟수를 확인하여 발급 가능 여부를 반환합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공 (result.canIssue 값이 true일 때만 버튼 활성화)")
    public ResponseEntity<ApiResponse<EventCheckResponse>> checkEligibility(
            Authentication authentication, @RequestParam Long applicationId) {
        return ResponseEntity.ok()
                .body(
                        ApiResponse.onSuccessOK(
                                couponService.checkEligibility(
                                        authentication.getName(), applicationId)));
    }

    // 멘토 - QR 코드 이미지 생성
    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(
            summary = "멘토 인증용 QR 코드 생성",
            description = "멘토가 본인의 커피챗 ID를 기반으로 오프라인 인증용 QR 코드 이미지(PNG)를 발급받습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "QR 코드 PNG 이미지 바이트 배열 반환")
    public ResponseEntity<byte[]> generateQr(
            @RequestParam Long applicationId, Authentication authentication) {
        String qrToken = couponService.generateQrToken(authentication.getName(), applicationId);
        byte[] qrImage = QrCodeUtil.generateQrCodeImage(qrToken, 250, 250);

        return ResponseEntity.ok(qrImage);
    }

    // 멘티 - QR 코드 스캔 시 유효성 검증 (1차 단계)
    @PostMapping("/verify-qr")
    @Operation(
            summary = "QR 코드 유효성 1차 검증",
            description =
                    "멘티가 스캔한 QR 토큰의 유효성, 만료 여부 및 본인 참여 여부를 1차적으로 확인합니다. 성공 시 프론트엔드에서 PIN 입력창을 띄웁니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "검증 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "EVENT_400_2: QR 만료/유효하지 않음 | USER_404: 유저 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description =
                    "EVENT_403_2: 멘티가 아님 | EVENT_403_3: 타인 커피챗 스캔 | EVENT_403_4: 참여 횟수 초과(2회)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ApiResponse<Void>> verifyQr(
            @RequestParam String qrToken, Authentication authentication) {
        couponService.verifyQrToken(authentication.getName(), qrToken);
        return ResponseEntity.ok(ApiResponse.onSuccessOK(null));
    }

    // 멘티 - 매장 PIN 번호 인증 및 최종 쿠폰 발급 (2차 단계)
    @PostMapping("/coupons")
    @Operation(
            summary = "최종 쿠폰 발급 (PIN 인증)",
            description = "매장 직원의 핀 번호를 입력받아 최종 검증 후 순차적인 쿠폰 번호를 발급합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "발급된 쿠폰 번호(String) 반환")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "EVENT_400_1: 핀 번호 불일치 | EVENT_400_2: QR 만료/유효하지 않음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "EVENT_409: 이미 쿠폰이 발급된 커피챗",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "410",
            description = "EVENT_410: 쿠폰 재고 소진",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "503",
            description = "EVENT_503: 동시성 처리 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ApiResponse<Map<String, String>>> issueCoupon(
            @RequestParam String qrToken,
            @RequestParam String storePin,
            Authentication authentication) {
        String couponNumber =
                couponService.verifyPinAndIssueCoupon(authentication.getName(), qrToken, storePin);
        return ResponseEntity.ok(ApiResponse.onSuccessOK(Map.of("couponNumber", couponNumber)));
    }

    @GetMapping("/status")
    @Operation(summary = "이벤트 진행 상태 조회", description = "현재 쿠폰 잔여 수량을 확인하여 이벤트 진행 상태를 반환합니다.")
    public ResponseEntity<ApiResponse<EventStatusResponse>> getEventStatus() {
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(couponService.getEventStatus()));
    }

    // ************ 관리자용 api ************

    @PostMapping("/admin/setup")
    @Operation(
            summary = "이벤트 쿠폰 수량 설정 (관리자)",
            description = "이벤트 전체 쿠폰 수량을 설정하고 발급 카운트를 0으로 초기화합니다.")
    public ResponseEntity<ApiResponse<String>> setupEvent(@RequestParam long maxCoupons) {
        couponSetupService.setupCouponLimit(maxCoupons);
        return ResponseEntity.ok()
                .body(ApiResponse.onSuccessOK("총 " + maxCoupons + "개의 쿠폰으로 이벤트가 설정되었습니다."));
    }

    @GetMapping("/admin/status")
    @Operation(summary = "이벤트 상세 상태 조회 (관리자)", description = "현재 전체 수량 대비 남은 쿠폰 수량을 조회합니다.")
    public ResponseEntity<ApiResponse<EventStatusResponse>> getAdminEventStatus() {
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(couponService.getEventStatus()));
    }
}
