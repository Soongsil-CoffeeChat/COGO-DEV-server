package com.soongsil.CoffeeChat.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppleTokenInfoResponse {
    private String sub; // 사용자 고유 식별자
    private String email; // 사용자 이메일
    private Boolean emailVerified; // 이메일 검증 여부
    private Boolean isPrivateEmail; // 개인정보 보호 이메일 여부

    public boolean isValid() {
        return sub != null && !sub.isEmpty();
    }
}
