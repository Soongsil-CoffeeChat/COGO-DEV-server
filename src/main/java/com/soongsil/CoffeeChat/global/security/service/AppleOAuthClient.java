package com.soongsil.CoffeeChat.global.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.dto.oauth2TokenResponse.AppleTokenResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppleOAuthClient {

    private final WebClient webClient;

    @Value("${spring.apple.token-url}")
    private String tokenUrl;

    @Value("${spring.apple.web-service-id}")
    private String serviceId;

    public AppleTokenResponse exchangeCode(
            String code, String clientSecret, String redirectUri, String codeVerifier) {

        // redirect uri 누락 방지
        if (redirectUri == null || redirectUri.isBlank()) {
            throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("client_id", serviceId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);

        // 필순가?
        if (codeVerifier != null && !codeVerifier.isBlank()) {
            form.add("code_verifier", codeVerifier);
        }

        return webClient
                .post()
                .uri(tokenUrl)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(AppleTokenResponse.class)
                .block();
    }
}
