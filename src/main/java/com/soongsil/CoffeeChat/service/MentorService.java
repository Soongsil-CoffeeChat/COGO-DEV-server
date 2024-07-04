package com.soongsil.CoffeeChat.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.PossibleDateRequestDto;
import com.soongsil.CoffeeChat.dto.ResponseMentorListInfo;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.PossibleDate;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.MentorRepository;
import com.soongsil.CoffeeChat.repository.UserRepository;

@Service
public class MentorService {
	private final MentorRepository mentorRepository;
	private final UserRepository userRepository;

	public MentorService(MentorRepository mentorRepository,
		UserRepository userRepository) {
		this.mentorRepository = mentorRepository;
		this.userRepository = userRepository;
	}

	//사용되는 공통 private method
	private List<Mentor> findMentorListByPart(String part) {
		return mentorRepository.findAllByPart(part);
	}

	public List<ResponseMentorListInfo> getMentorDtoListByPart(String part) {
		List<Mentor> mentorList = findMentorListByPart(part);  //파트에 해당하는 멘토 전부 가져오기
		List<ResponseMentorListInfo> dtoList = new ArrayList<>();
		for (Mentor mentor : mentorList) {  //찾아온 멘토들의 유저정보, 멘토정보를 가져와서 필요한 데이터들 dto로 생성
			User user = userRepository.findByMentor(mentor);
			dtoList.add(ResponseMentorListInfo.toDto(mentor, user));
		}
		return dtoList;
	}

	public List<PossibleDateRequestDto> findPossibleDateListByMentor(String username) {
		User user = userRepository.findByUsername(username);
		Mentor mentor = user.getMentor();
		Set<PossibleDate> possibleDateSet = mentor.getPossibleDates();
		Iterator<PossibleDate> iter = possibleDateSet.iterator();
		List<PossibleDateRequestDto> dtoList = new ArrayList<>();
		while (iter.hasNext()) {
			dtoList.add(PossibleDateRequestDto.toDto(iter.next()));
		}
		return dtoList;
	}

	@Transactional
	public Mentor saveUserPicture(String username, String picture){
		User user = userRepository.findByUsername(username);
		Mentor mentor = user.getMentor();
		mentor.setPicture(picture);
		return mentorRepository.save(mentor);
	}
}
