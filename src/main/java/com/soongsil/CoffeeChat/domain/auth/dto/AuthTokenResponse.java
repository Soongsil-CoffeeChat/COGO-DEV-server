package com.soongsil.CoffeeChat.domain.auth.dto;

import com.soongsil.CoffeeChat.domain.user.enums.UserAccountStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthTokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
    private UserAccountStatus accountStatus;
}
