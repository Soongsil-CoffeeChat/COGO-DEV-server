package com.soongsil.CoffeeChat.global.security.jwt;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.global.security.apple.AppleProperties;

import io.jsonwebtoken.Jwts;

@Component
public class AppleJwtGenerator {

    private final AppleProperties appleProperties;
    private static final String APPLE_AUDIENCE = "https://appleid.apple.com";

    @Autowired
    public AppleJwtGenerator(AppleProperties appleProperties) { // ResourceLoader 삭제
        this.appleProperties = appleProperties;
    }

    public String createClientSecret() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600_000); // 유효시간: 1시간

        return Jwts.builder()
                .header()
                .keyId(appleProperties.getKeyId())
                .and()
                .subject(appleProperties.getClientId())
                .issuer(appleProperties.getTeamId())
                .audience()
                .add(APPLE_AUDIENCE)
                .and()
                .expiration(expiration)
                .signWith(getPrivateKey(), Jwts.SIG.ES256) // ES256 → RS256로 변경
                .compact();
    }

    private PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String keyContent = appleProperties.getPrivateKey();

        if (keyContent == null || keyContent.isBlank()) {
            throw new IllegalArgumentException("Apple private key is not configured!");
        }

        String key =
                keyContent
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s+", "");

        byte[] encoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("EC"); // RSA 알고리즘 사용

        return keyFactory.generatePrivate(keySpec);
    }
}
