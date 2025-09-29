package com.soongsil.CoffeeChat.global.security.service;

import static java.time.temporal.ChronoUnit.DAYS;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/*
 * .p8 (EC키) 이용 최대 6개월 유효 client_secret 생성
 * Apple client_secret (ES256) 생성
 * 요구사항 : ES256으로 서명된 JWT Compact 문자열
 * Nimbus 이용하여 JWS 서명된 JWT 생성
 * Signed JWT = JWT(claim) + JWS (Signed)
 */
@Service
public class AppleClientSecretService {

    @Value("${spring.apple.team-id}")
    private String teamId;

    @Value("${spring.apple.key-id}")
    private String keyId;

    @Value("${spring.apple.web-service-id}")
    private String serviceId;

    @Value("${spring.apple.private-key}")
    private String privateKeyPem;

    public String createClientSecret() {
        try {
            // ECDSA 알고리즘
            ECPrivateKey privateKey = loadPrivateKey(privateKeyPem);

            // JWS 헤더
            JWSHeader header = new JWSHeader
                    .Builder(JWSAlgorithm.ES256)
                    .keyID(keyId)
                    .build();

            Instant now = Instant.now();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer(teamId)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(150, DAYS)))
                    .audience("https://appleid.apple.com")
                    .subject(serviceId)
                    .build();

            SignedJWT jwt = new SignedJWT(header, claims);
            JWSSigner signer = new ECDSASigner(privateKey);
            jwt.sign(signer);

            // JWS Compact 문자열 직렬화 -> client_secret
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create Apple client_secret", e);
        }
    }

    // 정규화 + Base64 디코딩 => PKCS#8 이용하여 ECPrivateKey 객체로 팩토링
    private ECPrivateKey loadPrivateKey(String pem) throws Exception {
        String content =
                pem.replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");
        byte[] pkcs8 = Base64.getDecoder().decode(content);
        var keySpec = new PKCS8EncodedKeySpec(pkcs8);
        var kf = KeyFactory.getInstance("EC");
        return (ECPrivateKey) kf.generatePrivate(keySpec);
    }
}
