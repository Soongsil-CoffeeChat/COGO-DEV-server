package com.soongsil.CoffeeChat.global.security.apple;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AppleKeyConfig {
    private final AppleProperties appleProperties;

    // private final ResourceLoader resourceLoader;

    public AppleKeyConfig(AppleProperties appleProperties) {
        this.appleProperties = appleProperties;
        // this.resourceLoader = resourceLoader;
    }

    @Bean
    public ECPrivateKey applePrivateKey() throws Exception {
        String key =
                appleProperties
                        .getPrivateKey()
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s+", "");
        log.info("Apple PrivateKey Base64 내용:\n{}", key);
        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return (ECPrivateKey) kf.generatePrivate(spec);
    }
}
