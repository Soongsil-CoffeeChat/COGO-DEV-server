package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.entity.*;
import com.soongsil.CoffeeChat.enums.ApplicationStatus;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

	@Autowired
	private RedisTemplate<String, String> redisTemplate;


	@Transactional
	public ApplicationCreateResponse createApplication(ApplicationCreateRequest request, String userName) throws Exception {
		System.out.println("여긴들어옴");
		String lockKey = "lock:" + request.getMentorId() + ":" +request.getDate()+":"+ request.getStartTime();
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		boolean isLockAcquired = valueOperations.setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS);
		if (!isLockAcquired) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Lock을 획득하지 못하였습니다.");  //409반환
		}

		try {
			System.out.println("mentorid: " + request.getMentorId() + ", " + request.getDate() + ", " + request.getStartTime() + ", " + request.getEndTime());
			User findMentorUser = userRepository.findByMentorIdWithFetch(request.getMentorId());
			Mentor findMentor = findMentorUser.getMentor();
			User findMenteeUser = userRepository.findByUsername(userName);
			Mentee findMentee = findMenteeUser.getMentee();

			LocalTime startTime = request.getStartTime();
			LocalDate date = request.getDate();

			// possibleDate 불러오는 JPQL
			TypedQuery<PossibleDate> query = em.createQuery(
					"SELECT p FROM PossibleDate p JOIN p.mentor m WHERE m.id = :mentorId AND p.startTime = :startTime AND p.date = :date",
					PossibleDate.class);
			query.setParameter("mentorId", request.getMentorId());
			query.setParameter("startTime", startTime);
			query.setParameter("date", date);

			Optional<PossibleDate> possibleDateOpt = query.getResultList().stream().findFirst();

			if (possibleDateOpt.isPresent()) {
				PossibleDate possibleDate = possibleDateOpt.get();
				if (!possibleDate.isActive()) {
					throw new ResponseStatusException(HttpStatus.GONE, "이미 신청된 시간입니다.");  //410 반환
				}
				System.out.println("possibleDate.getId() = " + possibleDate.getId());
				possibleDate.setActive(false);
				possibleDateRepository.save(possibleDate);
			} else {
				throw new Exception("NOT FOUND");
			}

			Application savedApplication = applicationRepository.save(request.toEntity(findMentor, findMentee));

			ApplicationService proxy = applicationContext.getBean(ApplicationService.class);
			proxy.sendApplicationMatchedEmailAsync(findMenteeUser.getEmail(), findMentorUser.getName(),
					findMenteeUser.getName(), savedApplication.getDate(), savedApplication.getStartTime(),
					savedApplication.getEndTime());

			return ApplicationCreateResponse.from(savedApplication);
		} finally {
			redisTemplate.delete(lockKey);
		}
	}

	@Async("mailExecutor")
	public void sendApplicationMatchedEmailAsync(String email, String mentorName, String menteeName, LocalDate date, LocalTime startTime, LocalTime endTime) throws MessagingException {
		emailUtil.sendApplicationMatchedEmail(email, mentorName, menteeName, date, startTime, endTime);
	}

	//동시성 테스트용
	private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
	private static final AtomicInteger transactionCounter = new AtomicInteger(0);  //트랜잭션마다 ID부여
	@Transactional
	public Application createApplicationIfPossible(Long possibleDateId, Mentor mentor, Mentee mentee) throws Exception {
		int transactionId = transactionCounter.incrementAndGet();  //트랜잭션 ID 1씩 증가하며 부여
		MDC.put("transactionId", String.valueOf(transactionId));  //로그에 트랜잭션ID 띄우기
		MDC.put("threadId", String.valueOf(Thread.currentThread().getId())); //로그에 스레드ID 띄우기

		try {
			logger.info("aaa트랜잭션 시작");

			PossibleDate possibleDate = em.find(PossibleDate.class, possibleDateId);

			if (possibleDate != null && possibleDate.isActive()) {  //Active상태면, Application생성
				possibleDate.setActive(false);  //중요! active상태를 false로 변경
				em.merge(possibleDate);

				Application application = Application.builder()
						.mentor(mentor)
						.mentee(mentee)
						.date(possibleDate.getDate())
						.startTime(possibleDate.getStartTime())
						.endTime(possibleDate.getEndTime())
						.accept(ApplicationStatus.UNMATCHED)
						.build();
				em.persist(application);

				logger.info("aaaApplication 생성: {}", application);
				return application;
			} else {
				logger.error("aaaAplication 생성 실패-Active하지 않음.");
				throw new Exception("The PossibleDate is already booked or does not exist.");
			}
		} catch (Exception e) {
			logger.error("aaaAplication 생성중 에러: ", e);
			throw e;
		} finally {
			logger.info("aaa트랜잭션 종료");
			MDC.clear();
		}
	}

}
