package com.soongsil.CoffeeChat.global.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.dto.oauth2TokenResponse.AppleTokenResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

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
            throw new GlobalException(GlobalErrorCode.OAUTH_MISSING_REDIRECT_URI);
        }
        if (code==null||code.isBlank()){
            throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("client_id", serviceId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);

        // 선택
        if (codeVerifier != null && !codeVerifier.isBlank()) {
            form.add("code_verifier", codeVerifier);
        }

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .exchangeToMono(res -> {
                    if (res.statusCode().is2xxSuccessful()) {
                        return res.bodyToMono(AppleTokenResponse.class);
                    }
                    return res.bodyToMono(String.class).defaultIfEmpty("")
                            .flatMap(body -> {
                                return Mono.error(
                                        new GlobalException(
                                                GlobalErrorCode.OAUTH_SERVICE_ERROR,
                                                new IllegalStateException("Apple token error" + res.statusCode() + " body" + body)
                                        )
                                );
                            });
                })
                .block();
    }
}
