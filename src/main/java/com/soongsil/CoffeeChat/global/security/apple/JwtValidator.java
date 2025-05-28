package com.soongsil.CoffeeChat.global.security.apple;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;

@Component
public class JwtValidator {
    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;

    private final ApplePublicKeyProvider keyProvider;

    public JwtValidator(ApplePublicKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    public SignedJWT validate(String idToken) {
        try {
            SignedJWT jwt = SignedJWT.parse(idToken);
            JWSHeader header = jwt.getHeader();
            // Verify algorithm
            if (!JWSAlgorithm.RS256.equals(header.getAlgorithm())) {
                throw new IllegalArgumentException(
                        "Unexpected JWS algorithm: " + header.getAlgorithm());
            }
            // Fetch matching key and verify signature
            var jwk = keyProvider.getKeyById(header.getKeyID());
            JWSVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());
            if (!jwt.verify(verifier)) {
                throw new IllegalArgumentException("Invalid JWT signature");
            }
            // Validate claims: exp, iss, aud
            var claims = jwt.getJWTClaimsSet();
            Date now = new Date();
            if (claims.getExpirationTime() == null || now.after(claims.getExpirationTime())) {
                throw new IllegalArgumentException("Token expired");
            }
            if (!"https://appleid.apple.com".equals(claims.getIssuer())) {
                throw new IllegalArgumentException("Invalid issuer: " + claims.getIssuer());
            }
            if (!claims.getAudience().contains(clientId)) {
                throw new IllegalArgumentException("Invalid audience: " + claims.getAudience());
            }
            return jwt;
        } catch (ParseException | JOSEException e) {
            throw new IllegalArgumentException("Failed to validate ID token", e);
        }
    }
}
