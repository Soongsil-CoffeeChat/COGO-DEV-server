package com.soongsil.CoffeeChat.global.security.apple;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "social-login.provider.apple")
public class AppleProperties {
    private String clientId;
    private String teamId;
    private String keyId;
//    private String privateKeyLocation;
    private String privateKey;
    private String redirectUri;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

//    public String getPrivateKeyLocation() {
//        return privateKeyLocation;
//    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

//    public void setPrivateKeyLocation(String privateKeyLocation) {
//        this.privateKeyLocation = privateKeyLocation;
//    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
