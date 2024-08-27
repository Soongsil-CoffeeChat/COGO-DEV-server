package com.soongsil.CoffeeChat.service;

import java.util.List;
import java.util.stream.Collectors;

import com.soongsil.CoffeeChat.controller.exception.CustomException;
import com.soongsil.CoffeeChat.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetResponseDto;
import com.soongsil.CoffeeChat.dto.PossibleDateCreateRequestDto;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;

import static com.soongsil.CoffeeChat.controller.exception.enums.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PossibleDateService {
	private final PossibleDateRepository possibleDateRepository;
	private final UserRepository userRepository;

	private User findUserByUsername(String username){
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new CustomException(
						USER_NOT_FOUND.getHttpStatusCode(),
						USER_NOT_FOUND.getErrorMessage())
				);
	}

	@Transactional
	public PossibleDateCreateGetResponseDto createPossibleDate(PossibleDateCreateRequestDto dto,
		String username) {
		User user = findUserByUsername(username);
		Mentor mentor = user.getMentor();
		PossibleDate possibleDate = PossibleDate.from(dto);
		possibleDate.setMentor(mentor);
		mentor.addPossibleDate(possibleDate);
		possibleDateRepository.save(possibleDate);
		return PossibleDateCreateGetResponseDto.from(possibleDate);
	}

	public List<PossibleDateCreateGetResponseDto> findPossibleDateListByMentor(String username) {
		User user = findUserByUsername(username);
		Long mentorId = user.getMentor().getId();
		return possibleDateRepository.getPossibleDatesByMentorId(mentorId)
			.stream()
			.map(possibleDate -> PossibleDateCreateGetResponseDto.builder()
				.date(possibleDate.getDate())
				.startTime(possibleDate.getStartTime())
				.endTime(possibleDate.getEndTime())
				.possibledateId(possibleDate.getId())
				.build())
			.collect(Collectors.toList());
	}
}
