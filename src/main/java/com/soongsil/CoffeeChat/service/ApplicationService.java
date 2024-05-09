package com.soongsil.CoffeeChat.service;

import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.email.EmailUtil;
import com.soongsil.CoffeeChat.dto.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.dto.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.entity.Application;
import com.soongsil.CoffeeChat.entity.Mentee;
import com.soongsil.CoffeeChat.entity.Mentor;
import com.soongsil.CoffeeChat.entity.User;
import com.soongsil.CoffeeChat.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.MentorRepository;
import com.soongsil.CoffeeChat.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final MentorRepository mentorRepository;
	private final MenteeRepository menteeRepository;
	private final UserRepository userRepository;
	private final EmailUtil emailUtil;

	@Transactional
	public ApplicationCreateResponse createApplication(ApplicationCreateRequest request, String userName) throws
		Exception {
		Mentor findMentor = mentorRepository.findById(request.getMentorId())
			.orElseThrow(Exception::new);
		// User findMentorUser = userRepository.findByMentorId(findMentor.getId());
		User findMentorUser = userRepository.findByMentor(findMentor);
		User findMenteeUser = userRepository.findByUsername(userName);
		Mentee findMentee = menteeRepository.findById(findMenteeUser.getId())
			.orElseThrow(Exception::new);

		Application savedApplication = applicationRepository.save(request.toEntity(findMentor, findMentee));

		emailUtil.sendApplicationMatchedEmail(findMenteeUser.getEmail(), findMentorUser.getName(),
			findMenteeUser.getName(), savedApplication.getDate(), savedApplication.getStartTime(),
			savedApplication.getEndTime());

		return ApplicationCreateResponse.from(
			savedApplication
		);
	}
}
