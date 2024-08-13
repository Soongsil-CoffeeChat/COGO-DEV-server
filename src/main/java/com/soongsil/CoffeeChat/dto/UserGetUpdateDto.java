package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGetUpdateDto {
	private String name;
	private String email;
	private String phoneNum;

	public static UserGetUpdateDto toDto(User user) {
		return UserGetUpdateDto.builder()
			.name(user.getName())
			.email(user.getEmail())
			.phoneNum(user.getPhoneNum())
			.build();
	}
}
