package com.soongsil.CoffeeChat.global.security.apple;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;
import com.soongsil.CoffeeChat.global.security.dto.AppleTokenInfoResponse;
import com.soongsil.CoffeeChat.global.security.jwt.AppleJwtGenerator;

@Service
public class AppleTokenService {
    private static final String TOKEN_URL = "https://appleid.apple.com/auth/token";
    private final JwtValidator jwtValidator;
    private final AppleJwtGenerator appleJwtGenerator;
    private final RestTemplate restTemplate = new RestTemplate();
    private final RSAPrivateKey applePrivateKey;

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.apple.team-id}")
    private String teamId;

    @Value("${spring.security.oauth2.client.registration.apple.key-id}")
    private String keyId;

    @Value("${spring.security.oauth2.client.registration.apple.redirect-uri}")
    private String redirectUri;

    public AppleTokenService(
            AppleJwtGenerator appleJwtGenerator,
            RSAPrivateKey applePrivateKey,
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
        String clientSecret = appleJwtGenerator.createClientSecret();

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

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(TOKEN_URL, request, Map.class);
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

    /** Apple에 제출할 client_secret JWT 생성 */
    //    private String buildClientSecretJWT() {
    //        Instant now = Instant.now();
    //        JWTClaimsSet claims =
    //                new JWTClaimsSet.Builder()
    //                        .issuer(teamId)
    //                        .issueTime(Date.from(now))
    //                        .expirationTime(Date.from(now.plusSeconds(300))) // 5분 유효
    //                        .audience("https://appleid.apple.com")
    //                        .subject(clientId)
    //                        .build();
    //
    //        JWSHeader header =
    //                new JWSHeader.Builder(JWSAlgorithm.RS256)
    //                        .keyID(keyId)
    //                        .type(JOSEObjectType.JWT)
    //                        .build();
    //
    //        SignedJWT signedJWT = new SignedJWT(header, claims);
    //        try {
    //            JWSSigner signer = new RSASSASigner(applePrivateKey);
    //            signedJWT.sign(signer);
    //            return signedJWT.serialize();
    //        } catch (JOSEException e) {
    //            throw new IllegalStateException("Failed to create Apple client_secret JWT", e);
    //        }
    //    }
}
