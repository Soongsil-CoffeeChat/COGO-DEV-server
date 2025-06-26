package com.soongsil.CoffeeChat.global.security.apple;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

// 애플 공개 키를 주기적으로 가져와 캐싱
@Component
public class ApplePublicKeyProvider {
    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicReference<JWKSet> jwkSetRef = new AtomicReference<>();
    private Instant expiresAt;

    @PostConstruct
    public void init() throws ParseException {
        refreshKeys();
    }

    public synchronized JWK getKeyById(String kid) throws ParseException {
        if (expiresAt == null || Instant.now().isAfter(expiresAt)) {
            refreshKeys();
        }
        List<JWK> keys = jwkSetRef.get().getKeys();
        return keys.stream()
                .filter(k -> kid.equals(k.getKeyID()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unable to find key: " + kid));
    }

    private void refreshKeys() throws ParseException {
        Map<String, Object> response = restTemplate.getForObject(APPLE_KEYS_URL, Map.class);
        JWKSet jwkSet = JWKSet.parse(response);
        jwkSetRef.set(jwkSet);
        // Set expiration to 1 hour later
        expiresAt = Instant.now().plusSeconds(3600);
    }
}
