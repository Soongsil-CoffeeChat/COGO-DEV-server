package com.soongsil.CoffeeChat.dto;

import lombok.Getter;

@Getter
public class CreateMenteeRequest {
	private String phoneNum;
	private String birth;
	private int grade;
	private String major;
	private String memo;
}
