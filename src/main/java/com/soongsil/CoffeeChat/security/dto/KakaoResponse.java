package com.soongsil.CoffeeChat.security.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {
	private final Map<String, Object> attribute;

	public KakaoResponse(Map<String, Object> attribute) {
		this.attribute = attribute;
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	// This assumes that the 'id' field is directly on the root of the response.
	@Override
	public String getProviderId() {
		return attribute.get("id").toString();
	}

	// 'properties' is a nested map that contains 'nickname' and 'profile_image'.
	@Override
	public String getEmail() {
		// Kakao might not necessarily provide an email in every response,
		// so this method needs to handle the possibility of it being absent.
		if (attribute.containsKey("kakao_account")) {
			Map<String, Object> kakaoAccount = (Map<String, Object>)attribute.get("kakao_account");
			if (kakaoAccount.containsKey("email")) {
				return kakaoAccount.get("email").toString();
			}
		}
		return null; // or an empty string, depending on how you want to handle missing emails
	}

	@Override
	public String getName() {
		// 'nickname' inside 'properties'
		Map<String, Object> properties = (Map<String, Object>)attribute.get("properties");
		return properties.get("nickname").toString();
	}

	public String getProfileImage() {
		// 'profile_image' inside 'properties'
		Map<String, Object> properties = (Map<String, Object>)attribute.get("properties");
		if (properties.containsKey("profile_image")) {
			return properties.get("profile_image").toString();
		}
		return null; // or an empty string
	}
}
