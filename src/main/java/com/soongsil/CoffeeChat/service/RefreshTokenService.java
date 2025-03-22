package com.soongsil.CoffeeChat.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.ReissueDto;
import com.soongsil.CoffeeChat.entity.Refresh;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;
import com.soongsil.CoffeeChat.repository.RefreshRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserService userService;

    public void addRefreshEntity(String username, String refresh, Long expiredMs) {
        // Refresh객체를 DB에 저장(블랙리스트관리)

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    public ReissueDto reissueByRefreshToken2(String refresh) {
        System.out.println("들어옴");

        // 토큰이 refresh인지 확인 (발급 시 페이로드에 명시)

        // DB에 저장되어 있는지 확인
        jwtUtil.validateToken(refresh);

        String username = jwtUtil.getUsername(refresh);
        String role = userService.findUserByUsername(username).getRole();
        System.out.println("username = " + username);

        // Make new JWT

        String newAccess = jwtUtil.createJwt("access", username, role, 1800000000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // Refresh 토큰 저장: DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);

        ReissueDto reissueDto = new ReissueDto(newRefresh, newAccess);
        return reissueDto;
    }

    public Map<String, String> reissueByRefreshTokenWithResponseBody(String refreshToken) {
        // 1. Refresh 토큰 유효성 검증
        jwtUtil.validateToken(refreshToken);

        // 2. 토큰의 카테고리 확인 (refresh인지)
        String category = jwtUtil.getCategory(refreshToken);
        if (!"refresh".equals(category)) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);
        }

        // 3. DB에 저장된 Refresh 토큰인지 확인
        boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if (!isExist) {
            throw new GlobalException(GlobalErrorCode.JWT_INVALID_TOKEN);
        }

        // 4. Refresh 토큰에서 사용자 정보 추출
        String username = jwtUtil.getUsername(refreshToken);
        String role = userService.findUserByUsername(username).getRole();

        // 5. 새로운 Access 및 Refresh 토큰 생성
        String newAccessToken = jwtUtil.createJwt("access", username, role, 1800000000L); // 예: 30분
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L); // 예: 1일

        // 6. DB에서 기존 Refresh 토큰 삭제 및 새로운 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refreshToken);
        addRefreshEntity(username, newRefreshToken, 86400000L);

        // 7. 응답 본문에 새로운 토큰 포함
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return tokens;
    }
}
