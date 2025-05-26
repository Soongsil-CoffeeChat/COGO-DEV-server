package com.soongsil.CoffeeChat.global.security.dto.apple;

import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "appleApiClient", url = "https://appleid.apple.com/auth")
public interface AppleClient {
    @GetMapping("/keys")
    ApplePublicKeyResponse applgetAppleAuthPublicKey();
}
