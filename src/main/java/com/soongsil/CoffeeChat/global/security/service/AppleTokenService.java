package com.soongsil.CoffeeChat.global.security.service;

import java.net.URL;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.dto.oauth2TokenResponse.AppleTokenInfoResponse;

@Service
public class AppleTokenService {

    private static final String ISS = "https://appleid.apple.com";
    private static final String JWKS_URL = "https://appleid.apple.com/auth/keys";
    // 15분
    private static final long TTL_MILLIS = 15 * 60 * 1000L;

    @Value("${spring.apple.web-service-id}")
    private String servicesId;

    // 캐시
    private JWKSet cachedJwkSet;
    private long cachedAtMillis = 0L;

    public AppleTokenInfoResponse validateAndExtract(String idToken) {
        SignedJWT jwt = parse(idToken);
        verifySignature(jwt);
        AppleTokenInfoResponse payload = mapClaims(jwt);
        validateClaims(payload);
        return payload;
    }

    private SignedJWT parse(String token) {
        try {
            return SignedJWT.parse(token);
        } catch (ParseException e) {
            throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        }
    }

    private void verifySignature(SignedJWT jwt) {
        try {
            JWSHeader header = jwt.getHeader();
            if (!JWSAlgorithm.RS256.equals(header.getAlgorithm())) {
                throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
            }

            JWKSet jwkSet = loadJwks();
            List<JWK> candidates =
                    new JWKSelector(
                                    new JWKMatcher.Builder()
                                            .keyType(KeyType.RSA)
                                            .keyID(header.getKeyID())
                                            .build())
                            .select(jwkSet);

            if (candidates.isEmpty()) {
                candidates =
                        new JWKSelector(new JWKMatcher.Builder().keyType(KeyType.RSA).build())
                                .select(jwkSet);
            }

            boolean verified = false;
            for (JWK jwk : candidates) {
                RSAKey rsa = (RSAKey) jwk;
                if (jwt.verify(new RSASSAVerifier(rsa.toRSAPublicKey()))) {
                    verified = true;
                    break;
                }
            }
            if (!verified) throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        } catch (JOSEException e) {
            throw new GlobalException(GlobalErrorCode.OAUTH_SERVICE_ERROR);
        }
    }

    private AppleTokenInfoResponse mapClaims(SignedJWT jwt) {
        try {
            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            AppleTokenInfoResponse p = new AppleTokenInfoResponse();
            p.setIss(claims.getIssuer());
            p.setSub(claims.getSubject());

            List<String> audList = claims.getAudience();
            p.setAud(audList != null && !audList.isEmpty() ? audList.get(0) : null);

            p.setIat(
                    claims.getIssueTime() == null
                            ? null
                            : claims.getIssueTime().toInstant().getEpochSecond());
            p.setExp(
                    claims.getExpirationTime() == null
                            ? null
                            : claims.getExpirationTime().toInstant().getEpochSecond());

            Object email = claims.getClaim("email");
            p.setEmail(email == null ? null : String.valueOf(email));

            /*
             * 메일 관련 로직 필요시 넣기
             */

            return p;
        } catch (Exception e) {
            throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        }
    }

    private void validateClaims(AppleTokenInfoResponse c) {
        if (!ISS.equals(c.getIss())) {
            throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        }
        if (c.getAud() == null) {
            throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        }
        if (!servicesId.equals(c.getAud())) {
            throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        }
        if (c.getExp() == null || Instant.ofEpochSecond(c.getExp()).isBefore(Instant.now())) {
            throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        }
        if (c.getSub() == null || c.getSub().isBlank()) {
            throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_TOKEN);
        }
    }

    private synchronized JWKSet loadJwks() {
        long now = System.currentTimeMillis();
        if (cachedJwkSet != null && (now - cachedAtMillis) < TTL_MILLIS) {
            return cachedJwkSet;
        }
        try {
            JWKSet fetched = JWKSet.load(new URL(JWKS_URL));
            cachedJwkSet = fetched;
            cachedAtMillis = now;
            return fetched;
        } catch (Exception e) {
            if (cachedJwkSet != null) {
                return cachedJwkSet;
            }
            throw new GlobalException(GlobalErrorCode.OAUTH_SERVICE_ERROR);
        }
    }
}
