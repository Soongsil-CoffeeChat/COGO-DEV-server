package com.soongsil.CoffeeChat.global.security.dto.oauth2TokenResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppleTokenInfoResponse {
    private String iss; // 토큰 발행자 : 항상 https://appleid.apple.com
    private String sub; // 사용자 고유 식별자
    private String aud; // ios 번들 ID
    private Long iat; // 토큰 발급 시간
    private Long exp; // 토큰 만료 시간
    private String email; // 사용자 이메일

    @JsonProperty("email_verified")
    private Boolean emailVerified; // 이메일 검증 여부

    @JsonProperty("is_private_email")
    private Boolean isPrivateEmail; // 개인정보 보호 이메일 여부

//    public boolean isValid() {
//        return sub != null && !sub.isEmpty();
//    }
}
