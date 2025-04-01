package com.soongsil.CoffeeChat.global.security.oauth2;

import java.util.Map;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.soongsil.CoffeeChat.domain.dto.MobileTokenResponse;
import com.soongsil.CoffeeChat.domain.dto.UserConverter;
import com.soongsil.CoffeeChat.domain.entity.User;
import com.soongsil.CoffeeChat.domain.entity.enums.Role;
import com.soongsil.CoffeeChat.domain.repository.User.UserRepository;
import com.soongsil.CoffeeChat.domain.service.RefreshTokenService;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.dto.GoogleTokenInfoResponse;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.GoogleResponse;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.KakaoResponse;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.NaverResponse;
import com.soongsil.CoffeeChat.global.security.dto.oauth2Response.OAuth2Response;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static final String GOOGLE_TOKEN_INFO_URL =
            "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=";

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final WebClient webClient;

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
            default -> null;
        };
    }

    private String createUsername(OAuth2Response oAuth2Response) {
        return oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
    }

    @Transactional
    public MobileTokenResponse verifyGoogleToken(String accessToken) {
        log.debug("Verifying Google token");

        GoogleTokenInfoResponse tokenInfo = fetchGoogleTokenInfo(accessToken);
        validateTokenInfo(tokenInfo);

        String username = tokenInfo.getSub();

        // 사용자 정보 처리
        Optional<User> existingUser = userRepository.findByUsername(username);
        boolean isNewAccount = existingUser.isEmpty();
        Role role;

        if (isNewAccount) {
            User newUser = UserConverter.toEntity(username, tokenInfo);
            userRepository.save(newUser);
            role = Role.ROLE_USER;
        } else {
            role = existingUser.get().getRole();
        }

        return generateTokenResponse(username, role, isNewAccount);
    }

    private GoogleTokenInfoResponse fetchGoogleTokenInfo(String accessToken) {
        try {
            return webClient
                    .get()
                    .uri(
                            uriBuilder ->
                                    uriBuilder
                                            .path("/oauth2/v3/tokeninfo")
                                            .scheme("https")
                                            .host("www.googleapis.com")
                                            .queryParam("access_token", accessToken)
                                            .build())
                    .retrieve()
                    .bodyToMono(GoogleTokenInfoResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Google API error: {}, Status: {}", e.getMessage(), e.getStatusCode());
            throw new GlobalException(GlobalErrorCode.OAUTH_SERVICE_ERROR);
        } catch (Exception e) {
            log.error("Error fetching Google token info: {}", e.getMessage());
            throw new GlobalException(GlobalErrorCode.OAUTH_SERVICE_ERROR);
        }
    }

    private void validateTokenInfo(GoogleTokenInfoResponse tokenInfo) {
        if (tokenInfo == null || !tokenInfo.isValid()) {
            log.error("Invalid token info: {}", tokenInfo);
            throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);
        }
        log.info("Token info verified successfully");
    }

    // 토큰 생성 로직 통합
    private MobileTokenResponse generateTokenResponse(
            String username, Role role, boolean isNewAccount) {
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username, role);

        // Refresh 토큰 저장
        refreshTokenService.addRefreshEntity(username, refreshToken, 86400000L);

        return MobileTokenResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .isNewAccount(isNewAccount)
                .build();
    }
}
