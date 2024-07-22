package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.entity.*;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.soongsil.CoffeeChat.util.email.EmailUtil;
import com.soongsil.CoffeeChat.dto.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.dto.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {
	private final EntityManager em;
	private final ApplicationRepository applicationRepository;
	private final MentorRepository mentorRepository;
	private final MenteeRepository menteeRepository;
	private final UserRepository userRepository;
	private final PossibleDateRepository possibleDateRepository;
	private final EmailUtil emailUtil;

	@Autowired
	private ApplicationContext applicationContext; // 프록시를 통해 자신을 호출하기 위해 ApplicationContext 주입

	@Transactional
	public ApplicationCreateResponse createApplication(ApplicationCreateRequest request, String userName) throws Exception {
		// 필요한 엔티티들 영속성 컨텍스트에 모두 올리기
		System.out.println("mentorid: " + request.getMentorId() + ", " + request.getDate() + ", " + request.getStartTime() + ", " + request.getEndTime());
		User findMentorUser = userRepository.findByMentorIdWithFetch(request.getMentorId());
		Mentor findMentor = findMentorUser.getMentor();
		User findMenteeUser = userRepository.findByUsername(userName);
		Mentee findMentee = findMenteeUser.getMentee();

// 사용자가 신청한 시간대에 해당하는 PossibleDate의 Active 변경로직
		LocalTime startTime = request.getStartTime();

// 특정 mentorId와 startTime을 가진 PossibleDate 엔티티를 가져오는 JPQL 쿼리
		TypedQuery<PossibleDate> query = em.createQuery(
				"SELECT p FROM PossibleDate p JOIN p.mentor m WHERE m.id = :mentorId AND p.startTime = :startTime",
				PossibleDate.class);
		query.setParameter("mentorId", request.getMentorId());
		query.setParameter("startTime", startTime);

// 결과를 가져옴
		Optional<PossibleDate> possibleDateOpt = query.getResultList().stream().findFirst();

// 찾은 PossibleDate의 Active상태 변경
		if (possibleDateOpt.isPresent()) {
			PossibleDate possibleDate = possibleDateOpt.get();
			System.out.println("possibleDate.getId() = " + possibleDate.getId());
			possibleDate.setActive(false);
			possibleDateRepository.save(possibleDate);
		} else {
			throw new Exception("NOT FOUND"); // 500에러
		}

// 성사된 커피챗 생성후 반환
		Application savedApplication = applicationRepository.save(request.toEntity(findMentor, findMentee));

// 비동기 메일 발송 메서드를 프록시를 통해 호출
		ApplicationService proxy = applicationContext.getBean(ApplicationService.class);
		proxy.sendApplicationMatchedEmailAsync(findMenteeUser.getEmail(), findMentorUser.getName(),
				findMenteeUser.getName(), savedApplication.getDate(), savedApplication.getStartTime(),
				savedApplication.getEndTime());

		return ApplicationCreateResponse.from(savedApplication);
	}

	@Async("mailExecutor")
	public void sendApplicationMatchedEmailAsync(String email, String mentorName, String menteeName, LocalDate date, LocalTime startTime, LocalTime endTime) throws MessagingException {
		emailUtil.sendApplicationMatchedEmail(email, mentorName, menteeName, date, startTime, endTime);
	}

}
