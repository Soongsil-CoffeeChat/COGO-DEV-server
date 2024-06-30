package com.soongsil.CoffeeChat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMentorRequest {
	private String phoneNum;
	private String birth;
	private String part;

}
