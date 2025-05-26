package com.soongsil.CoffeeChat.global.security.apple;

import com.nimbusds.jwt.SignedJWT;
import com.soongsil.CoffeeChat.global.security.dto.AppleTokenInfoResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Map;

@Service
public class AppleTokenService {
    private final JwtValidator jwtValidator;
    private final RestTemplate restTemplate = new RestTemplate();

    public AppleTokenService(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    public AppleTokenInfoResponse processToken(Map<String, Object> attribute) throws ParseException {
        String idToken = (String) attribute.get("id_token");
        SignedJWT jwt = jwtValidator.validate(idToken);
        var claims = jwt.getJWTClaimsSet();
        return AppleTokenInfoResponse.builder()
                .sub(claims.getSubject())
                .email(claims.getStringClaim("email"))
                .emailVerified(claims.getBooleanClaim("email_verified"))
                .isPrivateEmail(claims.getBooleanClaim("is_private_email"))
                .build();
    }

    public Map<String, Object> refreshTokens(String refreshToken) {
        String url = "https://appleid.apple.com/auth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=refresh_token"
                + "&refresh_token=" + refreshToken
                + "&client_id=" + /* your client_id */ ""
                + "&client_secret=" + /* your JWT client_secret */ "";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(url, request, Map.class);
    }
}
