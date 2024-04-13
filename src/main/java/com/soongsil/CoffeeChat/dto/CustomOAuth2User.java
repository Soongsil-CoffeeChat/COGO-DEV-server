package com.soongsil.CoffeeChat.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {
	private final UserDTO userDTO;

	public CustomOAuth2User(UserDTO userDTO) {
		this.userDTO = userDTO;
	}

	@Override
	public Map<String, Object> getAttributes() {  //받은 데이터값 리턴
		//여러 소셜 로그인을 진행하면 받는 Attribute 형식이 다르므로 사용 X
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { //Role값리턴
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {

			@Override
			public String getAuthority() {

				return userDTO.getRole();
			}
		});
		return collection;
	}

	@Override
	public String getName() {
		return userDTO.getName();
	}

	public String getUsername() {  //스프링애플리케이션 서버 ID반환 메소드

		return userDTO.getUsername();
	}
}
