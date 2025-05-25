package com.soongsil.CoffeeChat.global.security.dto.oauth2Response;

import java.util.Map;

import com.soongsil.CoffeeChat.global.security.jwt.JwtUtils;

public class AppleResponse implements OAuth2Response {
    private final Map<String, Object> attribute;
    private final String sub;
    private final String email;
    private final String name;

    public AppleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;

        // id_token 파싱
        String idToken = (String) attribute.get("id_token");
        if (idToken != null) {
            Map<String, Object> payload = JwtUtils.decodeJwtPayload(idToken);
            this.sub = (String) payload.get("sub");
            this.email = (String) payload.get("email");
            this.name = (String) payload.get("name");
        } else {
            this.sub = (String) attribute.get("sub");
            this.email = (String) attribute.get("email");
            this.name = (String) attribute.get("name");
        }
    }

    @Override
    public String getProvider() {
        return "apple";
    }

    @Override
    public String getProviderId() {
        return sub;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }
}
