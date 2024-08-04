package com.soongsil.CoffeeChat.service;

import java.util.List;


import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

@Service
public class MentorService {
	private final MentorRepository mentorRepository;
	private final UserRepository userRepository;
	private final PossibleDateRepository possibleDateRepository;

	public MentorService(MentorRepository mentorRepository,
		UserRepository userRepository, PossibleDateRepository possibleDateRepository) {
		this.mentorRepository = mentorRepository;
		this.userRepository = userRepository;
		this.possibleDateRepository=possibleDateRepository;
	}

	public List<ResponseMentorListInfo> getMentorDtoListByPart(int part) {
		return mentorRepository.getMentorListByPart(part); //일반join
	}

	public List<ResponseMentorListInfo> getMentorDtoListByClub(int club) {
		return mentorRepository.getMentorListByClub(club); //일반join
	}

	public List<PossibleDateRequestDto> findPossibleDateListByMentor(String username) {
		return possibleDateRepository.getPossibleDatesByUsername(username);
	}


}
