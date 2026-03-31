package com.soongsil.CoffeeChat.domain.event;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.soongsil.CoffeeChat.global.util.QrCodeUtil;

import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<Map<String, String>> issueCoupon(
            @RequestParam String qrToken,
            @RequestParam String storePin,
            Authentication authentication) {
        String couponUrl =
                couponService.verifyQrAndIssueCoupon(qrToken, storePin, authentication.getName());
        return ResponseEntity.ok(Map.of("couponUrl", couponUrl));
    }
}
