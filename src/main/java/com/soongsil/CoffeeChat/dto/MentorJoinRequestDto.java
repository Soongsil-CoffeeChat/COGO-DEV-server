package com.soongsil.CoffeeChat.dto;

import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorJoinRequestDto {
	private PartEnum part;
	private ClubEnum club;
}
