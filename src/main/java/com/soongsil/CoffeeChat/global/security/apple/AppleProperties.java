package com.soongsil.CoffeeChat.global.security.apple;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "apple")
public class AppleProperties {
    private String clientId;
    private String teamId;
    private String keyId;
    private String privateKeyLocation;

    public String getClientId() {
        return clientId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getPrivateKeyLocation() {
        return privateKeyLocation;
    }
}
