package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.dto.CreateMenteeRequest;
import com.soongsil.CoffeeChat.dto.CreateMentorRequest;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(USER_URI)
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/join/mentor")
	public ResponseEntity<Mentor> joinWithMentor(Authentication authentication,
		@RequestBody CreateMentorRequest dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(
			userService.saveMentorInformation(authentication.getName(), dto)
		);
	}

	@PostMapping("/join/mentee")
	public ResponseEntity<Mentee> joinWithMentee(Authentication authentication,
		@RequestBody CreateMenteeRequest dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(
			userService.saveMenteeInformation(authentication.getName(), dto)
		);
	}

}
