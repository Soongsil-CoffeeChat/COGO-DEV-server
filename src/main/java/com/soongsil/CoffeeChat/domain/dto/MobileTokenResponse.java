package com.soongsil.CoffeeChat.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MobileTokenResponse {
    private String accessToken;
    private String refreshToken;
    private boolean isNewAccount;
}
