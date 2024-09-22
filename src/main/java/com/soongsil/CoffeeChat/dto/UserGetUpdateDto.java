package com.soongsil.CoffeeChat.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.soongsil.CoffeeChat.entity.User;

import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
public class UserGetUpdateDto {
	private String name;
	private String email;
	private String phoneNum;
	private String role;
	private PartEnum part;
	private ClubEnum club;
	private String image;

	public static UserGetUpdateDto toDto(User user) {
		return UserGetUpdateDto.builder()
			.name(user.getName())
			.email(user.getEmail())
			.phoneNum(user.getPhoneNum())
			.build();
	}

	@QueryProjection
	public UserGetUpdateDto(String name, String email, String phoneNum, String role, PartEnum part, ClubEnum club, String image){
		this.name=name;
		this.email=email;
		this.phoneNum=phoneNum;
		this.role=role;
		this.part=part;
		this.club=club;
		this.image=image;
	}
}
