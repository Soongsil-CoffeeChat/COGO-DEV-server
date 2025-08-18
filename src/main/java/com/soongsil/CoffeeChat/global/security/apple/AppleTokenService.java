package com.soongsil.CoffeeChat.global.security.apple;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.soongsil.CoffeeChat.global.security.dto.AppleTokenInfoResponse;
import com.soongsil.CoffeeChat.global.security.jwt.AppleJwtGenerator;

@Service
public class AppleTokenService {
    private static final String TOKEN_URL = "https://appleid.apple.com/auth/token";
    private final JwtValidator jwtValidator;
    private final AppleJwtGenerator appleJwtGenerator;
    private final RestTemplate restTemplate = new RestTemplate();

    private final ECPrivateKey applePrivateKey;

    private static final Logger logger = LoggerFactory.getLogger(AppleTokenService.class);

    @Value("${social-login.provider.apple.client-id}")
    private String clientId;

    @Value("${social-login.provider.apple.team-id}")
    private String teamId;

    @Value("${social-login.provider.apple.key-id}")
    private String keyId;

    @Value("${social-login.provider.apple.redirect-uri}")
    private String redirectUri;

    public AppleTokenService(
            AppleJwtGenerator appleJwtGenerator,
            ECPrivateKey applePrivateKey,
            JwtValidator jwtValidator) {

        this.appleJwtGenerator = appleJwtGenerator;
        this.applePrivateKey = applePrivateKey;
        this.jwtValidator = jwtValidator;
    }

    /**
     * Authorization Code → Apple 토큰 교환
     *
     * @param code authorization code
     * @return token response map (access_token, id_token, refresh_token 등)
     */
    public Map<String, Object> exchangeCodeForTokens(String code)
            throws IOException,
                    NoSuchAlgorithmException,
                    InvalidKeySpecException,
                    InvalidKeyException {

        logger.debug("▶ exchangeCodeForTokens 시작 – code=[{}]", code);

        String clientSecret = appleJwtGenerator.createClientSecret();

        logger.debug("   생성된 clientSecret (일부)=[{}...]", clientSecret.substring(0, 10));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body =
                "grant_type=authorization_code"
                        + "&code="
                        + code
                        + "&redirect_uri="
                        + redirectUri
                        + "&client_id="
                        + clientId
                        + "&client_secret="
                        + clientSecret;
        logger.debug("   요청 body=[{}]", body);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, Map.class);
            logger.debug(
                    "   Apple 토큰 응답 status={}, body={}",
                    response.getStatusCode(),
                    response.getBody());
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error(
                    "   Apple 토큰 교환 실패: status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw e;
        } catch (RestClientException e) {
            logger.error("   Apple 토큰 교환 중 예외 발생", e);
            throw e;
        }
    }

    /** 토큰 갱신 (Refresh Token Grant) */
    public Map<String, Object> refreshTokens(String refreshToken)
            throws IOException,
                    NoSuchAlgorithmException,
                    InvalidKeySpecException,
                    InvalidKeyException {
        String clientSecret = appleJwtGenerator.createClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body =
                "grant_type=refresh_token"
                        + "&refresh_token="
                        + refreshToken
                        + "&client_id="
                        + clientId
                        + "&client_secret="
                        + clientSecret;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(TOKEN_URL, request, Map.class);
    }

    /** 받은 토큰맵에서 ID 토큰을 추출, 검증 후 사용자 정보 매핑 */
    public AppleTokenInfoResponse processToken(Map<String, Object> tokenMap) throws ParseException {
        String idToken = (String) tokenMap.get("id_token");
        // 서명 및 클레임 검증 (JwtValidator 내부에서 처리)
        var jwt = jwtValidator.validate(idToken);
        var claims = jwt.getJWTClaimsSet();
        return AppleTokenInfoResponse.builder()
                .sub(claims.getSubject())
                .email(claims.getStringClaim("email"))
                .emailVerified(claims.getBooleanClaim("email_verified"))
                .isPrivateEmail(claims.getBooleanClaim("is_private_email"))
                .build();
    }
}
