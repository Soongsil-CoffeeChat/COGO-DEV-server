package com.soongsil.CoffeeChat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetResponseDto;
import com.soongsil.CoffeeChat.dto.PossibleDateCreateRequestDto;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PossibleDateService {
	private final PossibleDateRepository possibleDateRepository;
	private final UserRepository userRepository;

	@Transactional
	public PossibleDateCreateGetResponseDto createPossibleDate(PossibleDateCreateRequestDto dto,
		String username) {
		Mentor mentor = userRepository.findByUsername(username).getMentor();
		PossibleDate possibleDate = PossibleDate.from(dto);
		possibleDate.setMentor(mentor);
		mentor.addPossibleDate(possibleDate);
		possibleDateRepository.save(possibleDate);
		return PossibleDateCreateGetResponseDto.from(possibleDate);
	}
}
