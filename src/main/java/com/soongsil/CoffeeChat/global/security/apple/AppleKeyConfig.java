package com.soongsil.CoffeeChat.global.security.apple;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class AppleKeyConfig {
    private final AppleProperties appleProperties;
    private final ResourceLoader resourceLoader;

    public AppleKeyConfig(AppleProperties appleProperties, ResourceLoader resourceLoader) {
        this.appleProperties = appleProperties;
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public RSAPrivateKey applePrivateKey() throws Exception {
        Resource privateKeyResource =
                resourceLoader.getResource(appleProperties.getPrivateKeyLocation());

        try (InputStream is = privateKeyResource.getInputStream()) {
            String key = appleProperties.getPrivateKeyLocation();

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(spec);
        }
    }
}
