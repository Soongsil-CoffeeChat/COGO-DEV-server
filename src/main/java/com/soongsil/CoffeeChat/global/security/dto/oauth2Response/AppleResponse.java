package com.soongsil.CoffeeChat.global.security.dto.oauth2Response;

import java.util.Map;

import com.soongsil.CoffeeChat.global.security.dto.AppleTokenInfoResponse;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtils;

public class AppleResponse implements OAuth2Response {
    private final Map<String, Object> attribute;
    private final AppleTokenInfoResponse tokenInfo;

    public AppleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;

        String idToken = (String) attribute.get("id_token");
        if (idToken != null) {
            Map<String, Object> payload = JwtUtils.decodeJwtPayload(idToken);
            this.tokenInfo =
                    AppleTokenInfoResponse.builder()
                            .sub((String) payload.get("sub"))
                            .email((String) payload.get("email"))
                            .emailVerified(
                                    Boolean.valueOf(String.valueOf(payload.get("email_verified"))))
                            .isPrivateEmail(
                                    Boolean.valueOf(
                                            String.valueOf(payload.get("is_private_email"))))
                            .build();
        } else {
            this.tokenInfo = AppleTokenInfoResponse.builder().build();
        }
    }

    @Override
    public String getProvider() {
        return "apple";
    }

    @Override
    public String getProviderId() {
        return tokenInfo.getSub();
    }

    @Override
    public String getEmail() {
        return tokenInfo.getEmail();
    }

    @Override
    public String getName() {
        return null;
    }

    public AppleTokenInfoResponse getTokenInfo() {
        return tokenInfo;
    }
}
