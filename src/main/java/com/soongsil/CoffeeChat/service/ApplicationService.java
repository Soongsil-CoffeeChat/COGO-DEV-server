package com.soongsil.CoffeeChat.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.dto.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.dto.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.MentorRepository;
import com.soongsil.CoffeeChat.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final MentorRepository mentorRepository;
	private final MenteeRepository menteeRepository;
	private final UserRepository userRepository;

	public ApplicationCreateResponse createApplication(ApplicationCreateRequest request, String userName) throws Exception {
		Mentor findMentor = mentorRepository.findById(request.getMentorId())
			.orElseThrow(Exception::new);
		Mentee findMentee = menteeRepository.findById(userRepository.findByUsername(userName).getId())
			.orElseThrow(Exception::new);

		return ApplicationCreateResponse.from(
			applicationRepository.save(request.toEntity(findMentor, findMentee))
		);
	}
}
