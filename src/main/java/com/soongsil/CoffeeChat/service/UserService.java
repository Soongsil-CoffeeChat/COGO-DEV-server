package com.soongsil.CoffeeChat.service;

import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.CreateMenteeRequest;
import com.soongsil.CoffeeChat.dto.CreateMentorRequest;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.MentorRepository;
import com.soongsil.CoffeeChat.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final MentorRepository mentorRepository;
	private final MenteeRepository menteeRepository;
	private final UserRepository userRepository;

	@Transactional
	public Mentor saveMentorInformation(String username, CreateMentorRequest dto) {
		User user = userRepository.findByUsername(username);
		if(!user.getRole().equals("ROLE_ADMIN")) user.setRole("ROLE_MENTOR");
		Mentor mentor = Mentor.from(dto);
		user.setMentor(mentor);
		return mentorRepository.save(mentor);
	}

	@Transactional
	public Mentee saveMenteeInformation(String username, CreateMenteeRequest dto) {
		User user = userRepository.findByUsername(username);
		if(!user.getRole().equals("ROLE_ADMIN")) user.setRole("ROLE_MENTEE");
		Mentee mentee = Mentee.from(dto);
		user.setMentee(mentee);
		//TODO:이메일 사용자가 지정한값으로 변경해야함
		user.setEmail(dto.getEmail());
		return menteeRepository.save(mentee);
	}
}
