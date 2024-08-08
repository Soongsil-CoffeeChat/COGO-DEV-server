package com.soongsil.CoffeeChat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
	private String role;
	private String name;
	private String username; //스프링 서버 내의 유저 아이디
}
