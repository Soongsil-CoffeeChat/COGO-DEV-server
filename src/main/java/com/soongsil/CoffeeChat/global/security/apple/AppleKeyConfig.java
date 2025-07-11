package com.soongsil.CoffeeChat.global.security.apple;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class AppleKeyConfig {
    private final AppleProperties appleProperties;

    //    private final ResourceLoader resourceLoader;

    public AppleKeyConfig(AppleProperties appleProperties, ResourceLoader resourceLoader) {
        this.appleProperties = appleProperties;
        //        this.resourceLoader = resourceLoader;
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
