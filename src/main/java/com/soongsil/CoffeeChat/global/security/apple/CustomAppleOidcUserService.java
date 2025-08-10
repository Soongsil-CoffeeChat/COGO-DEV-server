package com.soongsil.CoffeeChat.global.security.apple;

import java.util.HashMap;
import java.util.Map;

import jakarta.transaction.Transactional;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.domain.user.dto.UserConverter;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.AppleResponse;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.OAuth2Response;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomAppleOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserRepository userRepository;
    private final OidcUserService oidcDelegate = new OidcUserService();

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest oidcRequest) throws OAuth2AuthenticationException {
        String registrationId = oidcRequest.getClientRegistration().getRegistrationId();
        if (!"apple".equals(registrationId)) {
            throw new OAuth2AuthenticationException("Unsupported OIDC provider: " + registrationId);
        }

        // Spring이 OIDC id_token 검증 + JWKS 처리
        OidcUser oidcUser = oidcDelegate.loadUser(oidcRequest);

        // AppleResponse가 필요 시 사용할 수 있도록 id_token도 함께 전달
        Map<String, Object> attributes = new HashMap<>(oidcUser.getAttributes());
        if (oidcUser.getIdToken() != null) {
            attributes.put("id_token", oidcUser.getIdToken().getTokenValue());
        }

        OAuth2Response response = new AppleResponse(attributes);

        // 도메인 사용자 upsert
        String username = response.getProvider() + " " + response.getProviderId();
        userRepository
                .findByUsername(username)
                .map(
                        u -> {
                            u.updateUser(response);
                            return u;
                        })
                .orElseGet(() -> userRepository.save(UserConverter.toEntity(username, response)));

        // ★ OIDC 경로는 반드시 OidcUser 반환 (프레임워크 기대 타입)
        return oidcUser;
    }
}
