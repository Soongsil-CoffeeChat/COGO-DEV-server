package com.soongsil.CoffeeChat.dto.Oauth;

import java.util.Map;

public class NaverResponse implements OAuth2Response {
	private final Map<String, Object> attribute; //데이터 받을 Map

	public NaverResponse(Map<String, Object> attribute) {

		this.attribute = (Map<String, Object>)attribute.get("response");
	}

	@Override
	public String getProvider() {

		return "naver";
	}

	@Override
	public String getProviderId() {

		return attribute.get("id").toString();
	}

	@Override
	public String getEmail() {

		return attribute.get("email").toString();
	}

	@Override
	public String getName() {

		return attribute.get("name").toString();
	}
}
