package com.soongsil.CoffeeChat.global.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/*
 * Apple client_secret (ES256) 생성
 * .p8 (EC키) 이용 최대 6개월 유효 client_secret 생성
 */
@Service
public class AppleClientSecretService {

    @Value("${spring.apple.team-id}")
    private String teamId;

    @Value("${spring.apple.key-id}")
    private String keyId;

    @Value("${spring.apple.client-id}")
    private String clientId;

    @Value("${spring.apple.private-key}")
    private String privateKeyPem;

    public String createClientSecret(){
    }

    // 예외 여기서 묶을까
    private ECPrivateKey loadPrivateKey(String pem) throws Exception{
        String content=pem
                .replace("-----BEGIN PRIVATE KEY-----","")
                .replace("-----END PRIVATE KEY-----","")
                .replaceAll("\\s","");
        byte[] pkcs8= Base64.getDecoder().decode(content);
        var keySpec=new PKCS8EncodedKeySpec(pkcs8);
        var kf= KeyFactory.getInstance("EC");
        return (ECPrivateKey) kf.generatePrivate(keySpec);
    }
}
