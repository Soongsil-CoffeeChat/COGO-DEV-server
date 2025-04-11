package com.soongsil.CoffeeChat.domain.service;

import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.soongsil.CoffeeChat.domain.dto.AuthTokenResponse;
import com.soongsil.CoffeeChat.domain.dto.UserConverter;
import com.soongsil.CoffeeChat.domain.entity.Refresh;
import com.soongsil.CoffeeChat.domain.entity.User;
import com.soongsil.CoffeeChat.domain.entity.enums.Role;
import com.soongsil.CoffeeChat.domain.entity.enums.UserAccountStatus;
import com.soongsil.CoffeeChat.domain.repository.RefreshRepository;
import com.soongsil.CoffeeChat.domain.repository.User.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.dto.AppleTokenInfoResponse;
import com.soongsil.CoffeeChat.global.security.dto.GoogleTokenInfoResponse;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final WebClient webClient;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final UserService userService;

    @Value("${spring.jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${spring.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    /**
     * Google 액세스 토큰을 검증하고 사용자 인증을 처리합니다.
     *
     * @param accessToken Google에서 발급받은 액세스 토큰
     * @return 인증 토큰 응답
     */
    @Transactional
    public AuthTokenResponse verifyGoogleToken(String accessToken) {
        GoogleTokenInfoResponse tokenInfo = fetchGoogleTokenInfo(accessToken);
        validateTokenInfo(tokenInfo, "Google");

        String username = tokenInfo.getSub();
        return processUserAuthentication(
                username, () -> UserConverter.toEntity(username, tokenInfo));
    }

    /**
     * Apple 액세스 토큰을 검증하고 사용자 인증을 처리합니다.
     *
     * @param accessToken Apple에서 발급받은 액세스 토큰
     * @return 인증 토큰 응답
     */
    @Transactional
    public AuthTokenResponse verifyAppleToken(String accessToken) {
        AppleTokenInfoResponse tokenInfo = fetchAppleTokenInfo(accessToken);
        validateTokenInfo(tokenInfo, "Apple");

        String username = tokenInfo.getSub();
        return processUserAuthentication(
                username, () -> UserConverter.toEntity(username, tokenInfo));
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     *
     * @param refreshToken 기존 리프레시 토큰
     * @return 갱신된 인증 토큰 응답
     */
    @Transactional
    public AuthTokenResponse reissueToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        Role role = userService.findUserByUsername(username).getRole();

        // 새로운 토큰 생성
        String newAccessToken = jwtUtil.createAccessToken(username, role);
        String newRefreshToken = jwtUtil.createRefreshToken(username, role);

        // 기존 토큰 삭제 및 새 토큰 저장
        refreshRepository.deleteByRefresh(refreshToken);
        addRefreshEntity(username, newRefreshToken);

        return AuthTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiresIn(accessTokenExpiration)
                .build();
    }

    /**
     * 리프레시 토큰의 유효성을 검증합니다.
     *
     * @param refreshToken 검증할 리프레시 토큰
     * @throws GlobalException 토큰이 유효하지 않을 경우
     */
    private void validateRefreshToken(String refreshToken) {
        // 1. 토큰 유효성 검증
        jwtUtil.validateToken(refreshToken);

        // 2. 토큰의 카테고리 확인
        String category = jwtUtil.getCategory(refreshToken);
        if (!"refresh".equals(category)) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);
        }

        // 3. DB에 저장된 토큰인지 확인
        if (!refreshRepository.existsByRefresh(refreshToken)) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);
        }
    }

    /**
     * 사용자 인증 처리 로직을 통합 관리합니다.
     *
     * @param username 사용자 식별자
     * @param userCreator 새 사용자 생성 함수
     * @return 인증 토큰 응답
     */
    private AuthTokenResponse processUserAuthentication(
            String username, Supplier<User> userCreator) {
        Optional<User> existingUser = userRepository.findByUsernameWithDeleted(username);
        UserAccountStatus accountStatus;
        Role role;

        if (existingUser.isEmpty()) {
            // 새 계정 생성
            User newUser = userCreator.get();
            userRepository.save(newUser);
            role = Role.ROLE_USER;
            accountStatus = UserAccountStatus.NEW_ACCOUNT;
        } else {
            User user = existingUser.get();
            role = user.getRole();

            // 삭제된 계정이면 복구
            if (user.getIsDeleted()) {
                user.undoSoftDelete();
                accountStatus = UserAccountStatus.RESTORED_ACCOUNT;
            } else {
                accountStatus = UserAccountStatus.EXISTING_ACCOUNT;
            }
        }

        return createAuthenticationResponse(username, role, accountStatus);
    }

    /**
     * 리프레시 토큰 정보를 데이터베이스에 저장합니다.
     *
     * @param username 사용자 식별자
     * @param refresh 리프레시 토큰
     */
    private void addRefreshEntity(String username, String refresh) {
        Date expirationDate = new Date(System.currentTimeMillis() + refreshTokenExpiration);

        Refresh refreshEntity =
                Refresh.builder()
                        .username(username)
                        .refresh(refresh)
                        .expiration(expirationDate.toString())
                        .build();

        refreshRepository.save(refreshEntity);
    }

    /**
     * Google 토큰 정보를 가져옵니다.
     *
     * @param accessToken Google 액세스 토큰
     * @return 토큰 정보 응답
     */
    private GoogleTokenInfoResponse fetchGoogleTokenInfo(String accessToken) {
        return webClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .scheme("https")
                                        .host("www.googleapis.com")
                                        .path("/oauth2/v3/tokeninfo")
                                        .queryParam("access_token", accessToken)
                                        .build())
                .retrieve()
                .bodyToMono(GoogleTokenInfoResponse.class)
                .onErrorMap(
                        WebClientResponseException.class,
                        e -> new GlobalException(GlobalErrorCode.OAUTH_SERVICE_ERROR))
                .onErrorMap(
                        e -> !(e instanceof GlobalException),
                        e -> new GlobalException(GlobalErrorCode.OAUTH_SERVICE_ERROR))
                .block();
    }

    /**
     * Apple 토큰 정보를 가져옵니다.
     *
     * @param accessToken Apple 액세스 토큰
     * @return 토큰 정보 응답
     */
    private AppleTokenInfoResponse fetchAppleTokenInfo(String accessToken) {
        return webClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .scheme("https")
                                        .host("appleid.apple.com")
                                        .path("/auth/oauth2/v1/introspect")
                                        .queryParam("client_id", "YOUR_CLIENT_ID")
                                        .queryParam("token", accessToken)
                                        .build())
                .retrieve()
                .bodyToMono(AppleTokenInfoResponse.class)
                .onErrorMap(
                        WebClientResponseException.class,
                        e -> new GlobalException(GlobalErrorCode.OAUTH_SERVICE_ERROR))
                .onErrorMap(
                        e -> !(e instanceof GlobalException),
                        e -> new GlobalException(GlobalErrorCode.OAUTH_SERVICE_ERROR))
                .block();
    }

    /**
     * 토큰 정보의 유효성을 검증합니다.
     *
     * @param tokenInfo 토큰 정보 객체
     * @param provider 인증 제공자(Google, Apple 등)
     * @throws GlobalException 토큰이 유효하지 않을 경우
     */
    private void validateTokenInfo(Object tokenInfo, String provider) {
        boolean isValid = false;

        if (tokenInfo instanceof GoogleTokenInfoResponse) {
            isValid = tokenInfo != null && ((GoogleTokenInfoResponse) tokenInfo).isValid();
        } else if (tokenInfo instanceof AppleTokenInfoResponse) {
            isValid = tokenInfo != null && ((AppleTokenInfoResponse) tokenInfo).getSub() != null;
        }

        if (!isValid) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);
        }
    }

    /**
     * 사용자 인증 토큰 응답을 생성합니다.
     *
     * @param username 사용자 식별자
     * @param role 사용자 역할
     * @param accountStatus 계정 상태
     * @return 인증 토큰 응답
     */
    private AuthTokenResponse createAuthenticationResponse(
            String username, Role role, UserAccountStatus accountStatus) {
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username, role);

        // Refresh 토큰 저장
        addRefreshEntity(username, refreshToken);

        return AuthTokenResponse.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiration)
                .accountStatus(accountStatus)
                .build();
    }
}
