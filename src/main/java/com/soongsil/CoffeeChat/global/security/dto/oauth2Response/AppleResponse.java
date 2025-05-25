package com.soongsil.CoffeeChat.global.security.dto.oauth2Response;

import java.util.Map;

public class AppleResponse implements OAuth2Response{
    private final Map<String, Object> attribute;

    public AppleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "apple";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
