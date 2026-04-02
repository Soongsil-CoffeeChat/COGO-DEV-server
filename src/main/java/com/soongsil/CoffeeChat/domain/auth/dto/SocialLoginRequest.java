package com.soongsil.CoffeeChat.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialLoginRequest {
    private String accessToken;
}
