package com.soongsil.CoffeeChat.global.security.oauth2;

import com.soongsil.CoffeeChat.domain.auth.enums.Role;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.global.security.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {
    private final UserDto userDTO;

    public CustomOAuth2User(User user) {
        this.userDTO =
                UserDto.builder()
                        .username(user.getUsername())
                        .name(user.getName())
                        .role(user.getRole())
                        .build();
    }

    public CustomOAuth2User(String username, Role role) {
        this.userDTO = UserDto.builder().username(username).role(role).build();
    }

    @Override
    public Map<String, Object> getAttributes() { // 받은 데이터값 리턴
        // 여러 소셜 로그인을 진행하면 받는 Attribute 형식이 다르므로 사용 X
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Role값리턴
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(
                new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return userDTO.getRole().name();
                    }
                });
        return collection;
    }

    @Override
    public String getName() {
        return userDTO.getName();
    }

    public String getUsername() { // 스프링애플리케이션 서버 ID반환 메소드
        return userDTO.getUsername();
    }
}
