package com.soongsil.CoffeeChat.global.security.oauth2;

import java.util.Map;

import jakarta.transaction.Transactional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.domain.user.dto.UserConverter;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.GoogleResponse;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.KakaoResponse;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.NaverResponse;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.OAuth2Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response response;
        Map<String, Object> attributes;

        // ★ 애플은 OIDC 전용 서비스에서 처리하므로 여기서 막는다.
        if ("apple".equals(registrationId)) {
            throw new OAuth2AuthenticationException("Apple must be handled via OIDC");
        }

        OAuth2User oAuth2User = super.loadUser(userRequest);
        attributes = oAuth2User.getAttributes();

        response = createOAuth2Response(registrationId, attributes);
        if (response == null) {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        String username = response.getProvider() + " " + response.getProviderId();
        User user =
                userRepository
                        .findByUsername(username)
                        .map(
                                existing -> {
                                    existing.updateUser(response);
                                    return existing;
                                })
                        .orElseGet(
                                () ->
                                        userRepository.save(
                                                UserConverter.toEntity(username, response)));

        // 필요하면 new CustomOAuth2User(user)로 감싸도 됨
        return oAuth2User;
    }

    private OAuth2Response createOAuth2Response(
            String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "naver" -> new NaverResponse(attributes);
            case "google" -> new GoogleResponse(attributes);
            case "kakao" -> new KakaoResponse(attributes);
            default -> null;
        };
    }
}
