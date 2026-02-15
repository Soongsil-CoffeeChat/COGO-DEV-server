package com.soongsil.CoffeeChat.domain.push;

import lombok.Getter;

@Getter
public class DeviceTokenRequest {
    private String token;
    private Platform platform;
}
