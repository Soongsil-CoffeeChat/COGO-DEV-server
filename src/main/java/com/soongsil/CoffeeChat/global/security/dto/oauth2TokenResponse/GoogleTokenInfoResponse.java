package com.soongsil.CoffeeChat.global.security.dto.oauth2TokenResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenInfoResponse {
    private String iss; // 토큰 발행자
    private String azp; // 승인된 파티
    private String aud; // 토큰 대상자
    private String sub; // 사용자 ID
    private String email; // 사용자 이메일
    private Boolean emailVerified; // 이메일 인증 여부
    private String name; // 사용자 이름 (있는 경우)
    private String picture; // 프로필 사진 URL (있는 경우)
    private Long iat; // 토큰 발행 시간
    private Long exp; // 토큰 만료 시간

    public boolean isValid() {
        return sub != null && !sub.isEmpty();
    }
}
