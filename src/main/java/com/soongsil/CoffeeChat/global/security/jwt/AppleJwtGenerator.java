package com.soongsil.CoffeeChat.global.security.jwt;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soongsil.CoffeeChat.global.security.apple.AppleProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class AppleJwtGenerator {

    private final AppleProperties appleProperties;
    //    private final ResourceLoader resourceLoader;
    private final ECPrivateKey privateKey;
    private static final String APPLE_AUDIENCE = "https://appleid.apple.com";

    @Autowired
    public AppleJwtGenerator(AppleProperties appleProperties, ECPrivateKey applePrivateKey) {
        this.appleProperties = appleProperties;
        this.privateKey = applePrivateKey;
    }

    public String createClientSecret()
            throws IOException,
                    NoSuchAlgorithmException,
                    InvalidKeySpecException,
                    InvalidKeyException {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600_000); // 유효시간: 1시간

        //        return Jwts.builder()
        //                .header()
        //                .keyId(appleProperties.getKeyId())
        //                .and()
        //                .subject(appleProperties.getClientId())
        //                .issuer(appleProperties.getTeamId())
        //                .audience()
        //                .add(APPLE_AUDIENCE)
        //                .and()
        //                .expiration(expiration)
        //                .signWith(getPrivateKey(), Jwts.SIG.ES256)
        //                .compact();
        return Jwts.builder()
                .header()
                .add("kid", appleProperties.getKeyId())
                .and()
                .issuer(appleProperties.getTeamId())
                .subject(appleProperties.getClientId())
                .expiration(expiration)
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact();
    }

    //    private PrivateKey getPrivateKey()
    //            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    //        Resource resource =
    // resourceLoader.getResource(appleProperties.getPrivateKeyLocation());
    //        try (BufferedReader reader =
    //                new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
    //            String keyContent = reader.lines().collect(Collectors.joining("\n"));
    //            String key =
    //                    keyContent
    //                            .replace("-----BEGIN PRIVATE KEY-----", "")
    //                            .replace("-----END PRIVATE KEY-----", "")
    //                            .replaceAll("\\s+", "");
    //            byte[] encoded = Base64.getDecoder().decode(key);
    //            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
    //            KeyFactory keyFactory = KeyFactory.getInstance("EC");
    //            return keyFactory.generatePrivate(keySpec);
    //        }
    //    }
}
