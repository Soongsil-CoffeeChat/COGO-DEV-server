package com.soongsil.CoffeeChat.global.security.jwt;

import com.soongsil.CoffeeChat.global.security.apple.AppleProperties;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class AppleJwtGenerator {

    private final AppleProperties appleProperties;
    private final ResourceLoader resourceLoader;
    private static final String APPLE_AUDIENCE = "https://appleid.apple.com";

    @Autowired
    public AppleJwtGenerator(AppleProperties appleProperties, ResourceLoader resourceLoader) {
        this.appleProperties = appleProperties;
        this.resourceLoader = resourceLoader;
    }

    public String createClientSecret() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600_000);  // 유효시간: 1시간

        return Jwts.builder()
                .header().keyId(appleProperties.getKeyId()).and()
                .subject(appleProperties.getClientId())
                .issuer(appleProperties.getTeamId())
                .audience().add(APPLE_AUDIENCE).and()
                .expiration(expiration)
                .signWith(getPrivateKey(), Jwts.SIG.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource resource = resourceLoader.getResource(appleProperties.getPrivateKeyLocation());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String keyContent = reader.lines().collect(Collectors.joining("\n"));
            String key = keyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] encoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        }
    }
}
