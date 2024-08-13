package com.soongsil.CoffeeChat.service;

import static com.soongsil.CoffeeChat.controller.exception.enums.MentorErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.controller.exception.CustomException;
import com.soongsil.CoffeeChat.dto.MentorGetListResponseDto;
import com.soongsil.CoffeeChat.dto.MentorGetUpdateDetailDto;
import com.soongsil.CoffeeChat.dto.MentorUpdateRequestDto;
import com.soongsil.CoffeeChat.dto.PossibleDateCreateGetResponseDto;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.enums.ClubEnum;
import com.soongsil.CoffeeChat.enums.PartEnum;
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

	public List<MentorGetListResponseDto> getMentorDtoListByPart(PartEnum part) {
		return mentorRepository.getMentorListByPart(part); //일반join
	}

	public List<MentorGetListResponseDto> getMentorDtoListByClub(ClubEnum club) {
		return mentorRepository.getMentorListByClub(club); //일반join
	}

	public List<MentorGetListResponseDto> getMentorDtoListByPartAndClub(PartEnum part, ClubEnum club) {
		return mentorRepository.getMentorListByPartAndClub(part, club);
	}

	public List<PossibleDateCreateGetResponseDto> findPossibleDateListByMentor(Long mentorId) {
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

	public MentorGetUpdateDetailDto getMentorDtoById(Long mentorId) {
		//TODO: join으로 바꾸면될듯
		Mentor findMentor = mentorRepository.findById(mentorId)
			.orElseThrow(() -> new CustomException(
				MEMBER_NOT_FOUND.getHttpStatusCode(),
				MEMBER_NOT_FOUND.getErrorMessage())
			);
		return MentorGetUpdateDetailDto.of(
			findMentor,
			userRepository.findByMentor(findMentor)
		);
	}

	public MentorGetUpdateDetailDto getMentorDtoByIdWithJoin(Long mentorId) {
		return mentorRepository.getMentorInfoByMentorId(mentorId);
	}

	@Transactional
	public MentorGetUpdateDetailDto updateMentorInfo(String username, MentorUpdateRequestDto mentorUpdateRequestDto) {
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
