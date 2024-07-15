package com.soongsil.CoffeeChat.service;

import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PossibleDateService {
	private final PossibleDateRepository possibleDateRepository;
	private final UserRepository userRepository;

	public PossibleDateRequestDto createPossibleDate(PossibleDateRequestDto dto,
		String username) {
		User user = userRepository.findByUsername(username);
		Mentor mentor = user.getMentor();
		PossibleDate possibleDate = PossibleDate.from(dto);
		possibleDate.setMentor(mentor);
		mentor.addPossibleDate(possibleDate);
		possibleDateRepository.save(possibleDate);
		return dto;
	}
}
