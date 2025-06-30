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
import com.soongsil.CoffeeChat.global.security.apple.AppleTokenService;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.*;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final AppleTokenService appleTokenService;

    @SneakyThrows
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response response;
        Map<String, Object> attributes;

        if ("apple".equals(registrationId)) {

            // 클라이언트에서 받은 id_token만 처리
            String idToken = (String) userRequest.getAdditionalParameters().get("id_token");
            if (idToken == null) {
                throw new OAuth2AuthenticationException("Apple id_token is missing");
            }
            appleTokenService.processToken(Map.of("id_token", idToken));
            // attributes에 id_token만 담아서 넘김
            attributes = Map.of("id_token", idToken);
            response = new AppleResponse(attributes);
        } else {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            attributes = oAuth2User.getAttributes();
            response = createOAuth2Response(registrationId, attributes);
            if (response == null) {
                throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
            }
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

        return new CustomOAuth2User(user);
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
