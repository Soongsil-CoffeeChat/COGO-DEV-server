package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.entity.*;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.email.EmailUtil;
import com.soongsil.CoffeeChat.dto.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.dto.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.webjars.NotFoundException;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {
	private final EntityManager em;
	private final ApplicationRepository applicationRepository;
	private final MentorRepository mentorRepository;
	private final MenteeRepository menteeRepository;
	private final UserRepository userRepository;
	private final EmailUtil emailUtil;
	private final PossibleDateRepository possibleDateRepository;

	@Transactional
	public ApplicationCreateResponse createApplication(ApplicationCreateRequest request, String userName) throws
		Exception {
		//필요한 엔티티들 영속성 컨텍스트에 모두 올리기
		System.out.println("mentorid: "+request.getMentorId()+", "+request.getDate()+", "+request.getStartTime()+", "+request.getEndTime());
		User findMentorUser=userRepository.findByMentorIdWithFetch(request.getMentorId());
		Mentor findMentor=findMentorUser.getMentor();
		User findMenteeUser=userRepository.findByUsername(userName);
		Mentee findMentee=findMenteeUser.getMentee();

		//사용자가 신청한 시간대에 해당하는 PossibleDate의 Active 변경로직
		LocalTime startTime = request.getStartTime();
		// 영속성 컨텍스트에서 PossibleDate 엔티티를 모두 가져옴
		List<PossibleDate> possibleDates = em
				.createQuery("SELECT p FROM PossibleDate p", PossibleDate.class)
				.getResultList();

		// startTime이 지정된 시간인 엔티티를 필터링
		Optional<PossibleDate> possibleDateOpt= possibleDates.stream()
				.filter(p -> startTime.equals(p.getStartTime())).findFirst();
		//찾은 PossibleDate의 Active상태 변경
		if(possibleDateOpt.isPresent()) {
            PossibleDate possibleDate=possibleDateOpt.get();
			possibleDate.setActive(false);
			possibleDateRepository.save(possibleDate);
        }
		else throw new Exception("NOT FOUND"); //500에러

		//성사된 커피챗 생성후 반환
		Application savedApplication = applicationRepository.save(request.toEntity(findMentor, findMentee));

		emailUtil.sendApplicationMatchedEmail(findMenteeUser.getEmail(), findMentorUser.getName(),
			findMenteeUser.getName(), savedApplication.getDate(), savedApplication.getStartTime(),
			savedApplication.getEndTime());

		return ApplicationCreateResponse.from(
			savedApplication
		);
	}
}
