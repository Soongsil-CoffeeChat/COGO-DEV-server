package com.soongsil.CoffeeChat.global.security.apple;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppleKeyConfig {
    private final AppleProperties appleProperties;

    public AppleKeyConfig(AppleProperties appleProperties) {
        this.appleProperties = appleProperties;
    }

    @Bean
    public ECPrivateKey applePrivateKey() throws Exception {
        String baseKey = appleProperties.getPrivateKey();
        baseKey = baseKey.replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(baseKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);

        KeyFactory kf = KeyFactory.getInstance("EC");
        return (ECPrivateKey) kf.generatePrivate(spec);
    }
}
