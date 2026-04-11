package com.soongsil.CoffeeChat.global.dev;

import com.soongsil.CoffeeChat.global.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.soongsil.CoffeeChat.domain.auth.enums.Role;

import java.util.Map;

@RestController
@Profile("local")
@RequestMapping("/dev")
@RequiredArgsConstructor
@Tag(name = "DEV", description = "개발 환경 (profile=local) 전용 API")
public class DevAuthController {

    private final JwtUtil jwtUtil;

    private static final Map<Role, String> DEV_USERNAMES = Map.of(
            Role.ROLE_MENTOR, "dev_mentor_google_001",
            Role.ROLE_MENTEE, "dev_mentee_google_001",
            Role.ROLE_ADMIN, "dev_admin_google_001",
            Role.ROLE_USER, "dev_user_google_001"
    );

    @GetMapping("/token")
    @Operation(summary = "개발용 JWT 발급",
            description = "role 파라미터로 MENTOR/MENTEE/ADMIN 토큰 즉시 발급.")
    public ResponseEntity<DevTokenResponse> issueDevToken(
            @RequestParam Role role
    ) {
        String username = DEV_USERNAMES.get(role);
        String accessToken = jwtUtil.createAccessToken(username, role);
        String refreshToken = jwtUtil.createRefreshToken(username, role);

        return ResponseEntity.ok(new DevTokenResponse(accessToken, refreshToken, username, role.name()));
    }

    public record DevTokenResponse(
            String accessToken,
            String refreshToken,
            String username,
            String role
    ) {
    }
}
