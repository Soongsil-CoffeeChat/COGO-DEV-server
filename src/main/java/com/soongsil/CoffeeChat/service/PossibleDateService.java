package com.soongsil.CoffeeChat.service;

import static com.soongsil.CoffeeChat.controller.exception.enums.UserErrorCode.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.controller.exception.CustomException;
import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetResponseDto;
import com.soongsil.CoffeeChat.dto.PossibleDateCreateRequestDto;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PossibleDateService {
	private final PossibleDateRepository possibleDateRepository;
	private final UserRepository userRepository;

	private User findUserByUsername(String username) {
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new CustomException(
				USER_NOT_FOUND.getHttpStatusCode(),
				USER_NOT_FOUND.getErrorMessage())
			);
	}

	@Transactional
	public List<PossibleDateCreateGetResponseDto> updatePossibleDate(List<PossibleDateCreateRequestDto> dtos,
		String username) {

		User user = findUserByUsername(username);
		Mentor mentor = user.getMentor();

		// 가능 시간을 갱신하기 위해 모든 가능 시간을 삭제 후 새로운 값 삽입
		possibleDateRepository.deleteAllByMentor(mentor);
		log.info("[*] 멘토(" + username + ")의 가능시간 모두 삭제(가능시간 갱신 API 일부)");

		List<PossibleDate> possibleDates = dtos.stream()
			.map(dto -> {
				PossibleDate possibleDate = PossibleDate.from(dto);
				possibleDate.setMentor(mentor);
				mentor.addPossibleDate(possibleDate);
				return possibleDate;
			})
			.collect(Collectors.toList());

		possibleDateRepository.saveAll(possibleDates);

		return possibleDates.stream()
			.map(PossibleDateCreateGetResponseDto::from)
			.collect(Collectors.toList());
	}

	public List<PossibleDateCreateGetResponseDto> findPossibleDateListByMentor(Long mentorId) {
		// 2주로 설정
		LocalDate today = LocalDate.now();
		LocalDate twoWeeksLater = today.plusWeeks(2);

		return possibleDateRepository.getPossibleDatesByMentorId(mentorId)
			.stream()
			.filter(possibleDate -> !possibleDate.getDate().isBefore(today) && !possibleDate.getDate().isAfter(twoWeeksLater))
			.map(possibleDate -> PossibleDateCreateGetResponseDto.builder()
				.date(possibleDate.getDate())
				.startTime(possibleDate.getStartTime())
				.endTime(possibleDate.getEndTime())
				.possibledateId(possibleDate.getId())
				.build())
			.collect(Collectors.toList());
	}
}
