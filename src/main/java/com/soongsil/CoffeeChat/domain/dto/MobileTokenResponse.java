package com.soongsil.CoffeeChat.domain.dto;

import com.soongsil.CoffeeChat.domain.entity.enums.UserAccountStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MobileTokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
    private UserAccountStatus accountStatus;
}
