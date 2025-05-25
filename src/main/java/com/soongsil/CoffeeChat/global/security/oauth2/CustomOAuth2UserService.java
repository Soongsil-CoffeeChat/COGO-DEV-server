package com.soongsil.CoffeeChat.global.security.oauth2;

import java.util.Map;

import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.*;
import jakarta.transaction.Transactional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.domain.user.dto.UserConverter;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;

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
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response =
                createOAuth2Response(registrationId, oAuth2User.getAttributes());

        if (oAuth2Response == null) {
            throw new OAuth2AuthenticationException(
                    "Unsupported OAuth provider: " + registrationId);
        }

        String username = createUsername(oAuth2Response);

        // 유저 정보 가져오거나 새로 저장
        User user =
                userRepository
                        .findByUsername(username)
                        .map(
                                existingUser -> {
                                    // 소셜 정보로 사용자 정보 업데이트
                                    existingUser.updateUser(oAuth2Response);
                                    return existingUser; // @Transactional 내에서는 명시적 save 불필요
                                })
                        .orElseGet(
                                () ->
                                        userRepository.save(
                                                UserConverter.toEntity(username, oAuth2Response)));

        // CustomOAuth2User 반환
        return new CustomOAuth2User(user);
    }

    private OAuth2Response createOAuth2Response(
            String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "naver" -> new NaverResponse(attributes);
            case "google" -> new GoogleResponse(attributes);
            case "kakao" -> new KakaoResponse(attributes);
            case "apple" -> new AppleResponse(attributes);
            default -> null;
        };
    }

    private String createUsername(OAuth2Response oAuth2Response) {
        return oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
    }
}
