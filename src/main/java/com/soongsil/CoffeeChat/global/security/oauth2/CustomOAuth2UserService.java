package com.soongsil.CoffeeChat.global.security.oauth2;

import java.util.Map;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.soongsil.CoffeeChat.domain.dto.MobileTokenResponse;
import com.soongsil.CoffeeChat.domain.entity.User;
import com.soongsil.CoffeeChat.domain.entity.enums.Role;
import com.soongsil.CoffeeChat.domain.repository.User.UserRepository;
import com.soongsil.CoffeeChat.domain.service.RefreshTokenService;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.dto.UserDto;
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
    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private static final String GOOGLE_TOKEN_INFO_URL =
            "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=";
    private final RefreshTokenService refreshTokenService;

    // 리소스 서버에서 제공되는 유저정보 가져오기
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 리소스서버로부터 유저 데이터를 받아 소셜 형식에 맞게 데이터 전처리(DTO로)
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User = " + oAuth2User);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response;
        switch (registrationId) {
            case "naver" -> oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            case "google" -> oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            case "kakao" -> oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
            default -> {
                return null;
            }
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        // 유저가 DB에 있는지 확인 후 없으면 새로 저장
        Optional<User> existData = userRepository.findByUsername(username);
        if (existData.isEmpty()) {
            User user =
                    User.builder()
                            .username(username)
                            .email(oAuth2Response.getEmail())
                            .name(oAuth2Response.getName())
                            .role(Role.ROLE_USER)
                            .build();

            userRepository.save(user);

            UserDto userDTO = new UserDto();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(Role.ROLE_USER);

            return new CustomOAuth2User(userDTO);
        } else { // 데이터가 이미 존재하면 업데이트 후 OAuth2User객체로 반환
            // 소셜에서 로그인마다 업데이트를 선호하므로 로그인마다 DB 업데이트 진행
            existData.get().updateUser(oAuth2Response);
            userRepository.save(existData.get());

            UserDto userDTO = new UserDto();
            userDTO.setUsername(existData.get().getUsername());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.get().getRole());
            return new CustomOAuth2User(userDTO);
        }
    }

    public MobileTokenResponse verifyGoogleToken(String accessToken, String name) {
        log.info("[*] TOKEN>>>>> " + accessToken);
        RestTemplate restTemplate = new RestTemplate();
        String url = GOOGLE_TOKEN_INFO_URL + accessToken;
        try {
            Map<String, Object> tokenInfo = restTemplate.getForObject(url, Map.class);
            log.info("===== Token info received ===== " + tokenInfo);
            if (tokenInfo != null && tokenInfo.containsKey("sub")) {
                String email = (String) tokenInfo.get("email");
                String username = (String) tokenInfo.get("sub");

                // 사용자 정보 검색
                Optional<User> existingUser = userRepository.findByUsername(username);
                boolean isNewAccount;
                Role role;

                if (existingUser.isPresent()) {
                    // 기존 사용자: Role 정보 가져오기
                    isNewAccount = false;
                    role = existingUser.get().getRole();
                    log.info("Existing user found: " + username);
                } else {
                    isNewAccount = true;
                    role = Role.ROLE_USER;
                    log.info("New user created: " + username);

                    // 신규 사용자 정보 저장
                    User user =
                            User.builder()
                                    .username(username)
                                    .email(email)
                                    .name(name)
                                    .role(Role.ROLE_USER)
                                    .build();
                    userRepository.save(user);
                }

                // JWT 토큰 생성
                String newAccessToken = jwtUtil.createAccessToken(username, role);
                String newRefreshToken = jwtUtil.createRefreshToken(username, role);

                // Refresh 토큰을 Redis 또는 DB에 저장 (선택적)
                refreshTokenService.addRefreshEntity(username, newRefreshToken, 86400000L);

                // Access, Refresh 토큰 반환
                return MobileTokenResponse.builder()
                        .refreshToken(newRefreshToken)
                        .accessToken(newAccessToken)
                        .isNewAccount(isNewAccount)
                        .build();
            } else {
                log.error("===== Invalid token info ===== {}", tokenInfo);
                throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);
            }
        } catch (HttpClientErrorException e) {
            log.error("===== Error verifying Google token ===== {}", e.getResponseBodyAsString());
            throw new GlobalException(GlobalErrorCode.OAUTH_SERVICE_ERROR);
        }
    }
}
