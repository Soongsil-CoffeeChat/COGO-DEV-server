package com.soongsil.CoffeeChat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class MobileTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private boolean isNewAccount;
}
