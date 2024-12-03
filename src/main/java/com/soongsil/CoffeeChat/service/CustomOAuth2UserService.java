package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.config.jwt.JWTUtil;
import com.soongsil.CoffeeChat.controller.exception.CustomException;
import com.soongsil.CoffeeChat.dto.MobileTokenResponseDTO;
import com.soongsil.CoffeeChat.dto.Oauth.*;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.User.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.soongsil.CoffeeChat.controller.exception.enums.RefreshErrorCode.INVALID_TOKEN;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    private final JWTUtil jwtUtil;

    private static final String GOOGLE_TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=";
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    private User findUserByUsername(String username) {
        System.out.println("여기까지 들어옴");
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    //리소스 서버에서 제공되는 유저정보 가져오기
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //리소스서버로부터 유저 데이터를 받아 소셜 형식에 맞게 데이터 전처리(DTO로)
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User = " + oAuth2User);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            System.out.println("registrationId = " + registrationId);
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {

            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        //유저가 DB에 있는지 확인 후 없으면 새로 저장
        Optional<User> existData = userRepository.findByUsername(username);
        //User existData = findUserByUsername(username);
        if (existData.isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(oAuth2Response.getEmail());
            user.setName(oAuth2Response.getName());
            user.setRole("ROLE_USER");

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        } else {  //데이터가 이미 존재하면 업데이트 후 OAuth2User객체로 반환
            //소셜에서 로그인마다 업데이트를 선호하므로 로그인마다 DB 업데이트 진행
            existData.get().setEmail(oAuth2Response.getEmail());
            existData.get().setName(oAuth2Response.getName());
            userRepository.save(existData.get());

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.get().getUsername());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.get().getRole());
            return new CustomOAuth2User(userDTO);
        }
    }

    public MobileTokenResponseDTO verifyGoogleToken(String accessToken, String name) {
        log.info("[*] TOKEN>>>>> " + accessToken);
        RestTemplate restTemplate = new RestTemplate();
        String url = GOOGLE_TOKEN_INFO_URL + accessToken;
        try {
            Map<String, Object> tokenInfo = restTemplate.getForObject(url, Map.class);
            log.info("===== Token info received ===== " + tokenInfo);
            if (tokenInfo != null && tokenInfo.containsKey("sub")) {

                MobileUserDTO mobileUserDTO = MobileUserDTO.builder()
                        .email((String) tokenInfo.get("email"))
                        .username((String) tokenInfo.get("sub"))
                        .name(name)
                        .role("ROLE_USER")
                        .isNewAccount(false)
                        .build();

                if(!userRepository.findByUsername(mobileUserDTO.getUsername()).isPresent()){
                    mobileUserDTO.setNewAccount(true);
                }

                userService.saveMobileUser(mobileUserDTO);

                log.info("[*] USER>>>>> EMAIL[" + mobileUserDTO.getEmail(), "] NAME[" + mobileUserDTO.getUsername() + "] ROLE[" + mobileUserDTO.getRole() + "]");

                String newAccessToken = jwtUtil.createJwt("access", mobileUserDTO.getUsername(), "ROLE_USER", 1800000000L); // Access token (30분)
                String newRefreshToken = jwtUtil.createJwt("refresh", mobileUserDTO.getUsername(), "ROLE_USER", 86400000L); // Refresh token (24시간)

                // Refresh 토큰을 Redis 또는 DB에 저장 (선택적)
                refreshTokenService.addRefreshEntity(mobileUserDTO.getUsername(), newRefreshToken, 86400000L); // 24 hrs

                // access, refresh 토큰 반환
                return MobileTokenResponseDTO.builder()
                        .refreshToken(newRefreshToken)
                        .accessToken(newAccessToken)
                        .isNewAccount(mobileUserDTO.isNewAccount())
                        .build();
            } else {
                log.error("===== Invalid token info ===== " + tokenInfo);
                throw new CustomException(INVALID_TOKEN.getHttpStatusCode(), INVALID_TOKEN.getErrorMessage());
            }
        } catch (HttpClientErrorException e) {
            log.error("===== Error verifying Google token ===== " + e.getResponseBodyAsString());
            throw new CustomException(INVALID_TOKEN.getHttpStatusCode(), e.getResponseBodyAsString());
        }
    }
}
