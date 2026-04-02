package com.soongsil.CoffeeChat.domain.push.dto;

import com.soongsil.CoffeeChat.domain.push.entity.Platform;
import lombok.Getter;

@Getter
public class DeviceTokenRequest {
    private String token;
    private Platform platform;
}
