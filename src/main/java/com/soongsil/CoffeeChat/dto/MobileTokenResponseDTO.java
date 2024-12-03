package com.soongsil.CoffeeChat.dto;

import lombok.Builder;

@Builder
public class MobileTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private boolean isNewAccount;
}
