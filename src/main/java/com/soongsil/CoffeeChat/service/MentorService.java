package com.soongsil.CoffeeChat.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.UserRepository;

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


	@Transactional
	public List<ResponseMentorListInfo> getMentorDtoListByPart(String part) {
		return mentorRepository.getMentorListByPart(part); //일반join
		/* //fetch join
		List<ResponseMentorListInfo> dtos=new ArrayList<>();
		List<User> users=mentorRepository.getMentorListByPart2(part);
		for(User user:users){
			dtos.add(ResponseMentorListInfo.toDto(user.getMentor(), user));
		}
		return dtos;
		 */
	}

	public List<PossibleDateRequestDto> findPossibleDateListByMentor(String username) {
		return possibleDateRepository.getPossibleDatesByUsername(username);
	}

	@Transactional
	public Mentor saveUserPicture(String username, String picture){
		User user = userRepository.findByUsername(username);
		Mentor mentor = user.getMentor();
		mentor.setPicture(picture);
		return mentorRepository.save(mentor);
	}
}
