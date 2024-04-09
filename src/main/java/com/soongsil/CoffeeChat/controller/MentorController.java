package com.soongsil.CoffeeChat.controller;

import static com.soongsil.CoffeeChat.enums.RequestUri.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.service.MentorService;

@RequestMapping(MENTOR_URI)
@RestController
public class MentorController {
	private final MentorService mentorService;

	public MentorController(MentorService mentorService) {
		this.mentorService = mentorService;
	}

	@GetMapping("/{part}")
	public ResponseEntity<List<ResponseMentorListInfo>>
	getMentorListByPart(Authentication authentication, @PathVariable("part") String part) {
		return ResponseEntity.ok().body(mentorService.getMentorDtoListByPart(part));
	}

	@GetMapping("/possibleDates/{username}")
	public ResponseEntity<List<PossibleDateRequestDto>> getPossibleDates
		(Authentication authentication,
			@PathVariable("username") String username) {
		return ResponseEntity.ok().body(mentorService.findPossibleDateListByMentor(username));
	}

}
