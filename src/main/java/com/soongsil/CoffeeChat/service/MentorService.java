package com.soongsil.CoffeeChat.service;

import static com.soongsil.CoffeeChat.controller.exception.enums.MentorErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.controller.exception.CustomException;
import com.soongsil.CoffeeChat.controller.exception.enums.MentorErrorCode;
import com.soongsil.CoffeeChat.dto.MentorUpdateRequestDto;
import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.dto.ResponseMentorInfo;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorService {
	private final MentorRepository mentorRepository;
	private final UserRepository userRepository;
	private final PossibleDateRepository possibleDateRepository;

	public List<ResponseMentorListInfo> getMentorDtoListByPart(int part) {
		return mentorRepository.getMentorListByPart(part); //일반join
	}

	public List<ResponseMentorListInfo> getMentorDtoListByClub(int club) {
		return mentorRepository.getMentorListByClub(club); //일반join
	}

	public List<ResponseMentorListInfo> getMentorDtoListByPartAndClub(int part, int club) {
		return mentorRepository.getMentorListByPartAndClub(part, club);
	}

	public List<PossibleDateRequestDto> findPossibleDateListByMentor(Long mentorId) {
		return possibleDateRepository.getPossibleDatesById(mentorId);
	}

	public ResponseMentorInfo getMentorDtobyId(Long mentorId) {
		Mentor findMentor = mentorRepository.findById(mentorId)
			.orElseThrow(() -> new CustomException(
				MEMBER_NOT_FOUND.getHttpStatusCode(),
				MEMBER_NOT_FOUND.getErrorMessage())
			);
		return ResponseMentorInfo.of(
			findMentor,
			userRepository.findByMentor(findMentor)
		);
	}

	@Transactional
	public ResponseMentorInfo updateMentorInfo(String username, MentorUpdateRequestDto mentorUpdateRequestDto) {
		User findMentorUser = userRepository.findByUsername(username);
		User updatedMentorUser = User.builder()
			.id(findMentorUser.getId())
			.name(mentorUpdateRequestDto.getMentorName())
			.email(mentorUpdateRequestDto.getMentorEmail())
			.role(findMentorUser.getRole())
			.phoneNum(mentorUpdateRequestDto.getMentorPhoneNumber())
			.picture(findMentorUser.getPicture())
			.build();
		userRepository.save(updatedMentorUser);
		return null;
	}
}
