package com.soongsil.CoffeeChat.domain.event.controller;

import java.util.Map;

import com.soongsil.CoffeeChat.domain.event.service.CouponService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.domain.event.dto.EventStatusResponse;
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

    // 멘토- QR 코드 이미지 반환
    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(
            summary = "멘토 인증용 QR 코드 생성",
            description = "멘토가 본인의 커피챗 ID를 기반으로 오프라인 인증용 QR 코드 이미지(PNG)를 발급받습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "QR 코드 PNG 이미지 바이트 배열을 반환")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "USER_404: 유저 없음 | APPLICATION_404: 커피챗 정보 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "EVENT_403_1: 멘토가 아님 | EVENT_403_3: 본인 커피챗이 아님",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "EVENT_409: 이미 쿠폰이 발급된 커피챗",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<byte[]> generateQr(
            @RequestParam Long applicationId, Authentication authentication) {
        String qrToken = couponService.generateQrToken(applicationId, authentication.getName());
        byte[] qrImage = QrCodeUtil.generateQrCodeImage(qrToken, 250, 250);

        return ResponseEntity.ok(qrImage);
    }

    // 멘티- QR 검증 -> 직원 핀 번호 입력 후 최종 쿠폰 발급
    @PostMapping("/coupons")
    @Operation(
            summary = "멘티 쿠폰 발급 (QR 및 매장 핀 번호 인증)",
            description = "멘티가 스캔한 QR 토큰과 매장 직원의 핀 번호를 함께 전송하여 최종적으로 커피 쿠폰을 발급받습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "S3에서 꺼낸 쿠폰 이미지 URL을 반환")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "EVENT_400_1: 핀 번호 불일치 | EVENT_400_2: QR 만료/유효하지 않음 | USER_404: 유저 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description =
                    "EVENT_403_2: 멘티가 아님 | EVENT_403_3: 타인 커피챗 스캔 | EVENT_403_3: 참여 횟수 초과(2회)",
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
            description = "EVENT_503: 동시성 처리 오류 - 잠시 후 재시도 필요",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Map<String, String>> issueCoupon(
            @RequestParam String qrToken,
            @RequestParam String storePin,
            Authentication authentication) {
        String couponUrl =
                couponService.verifyQrAndIssueCoupon(qrToken, storePin, authentication.getName());
        return ResponseEntity.ok(Map.of("couponUrl", couponUrl));
    }

    @GetMapping("/status")
    @Operation(
            summary = "이벤트 진행 상태 조회",
            description =
                    "현재 쿠폰 잔여 수량을 확인하여 이벤트 진행 상태를 반환합니다. \n"
                            + "- IN_PROGRESS : 이벤트 진행 중 (쿠폰 여유 있음)\n"
                            + "- COMPLETED : 이벤트 완료 (쿠폰 재고 소진)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "이벤트 상태 및 잔여 수량 반환")
    public ResponseEntity<ApiResponse<EventStatusResponse>> getEventStatus() {
        return ResponseEntity.ok().body(ApiResponse.onSuccessOK(couponService.getEventStatus()));
    }
}
