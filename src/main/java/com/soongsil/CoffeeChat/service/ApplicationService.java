package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.dto.PerformanceRequest;
import com.soongsil.CoffeeChat.dto.PerformanceResult;
import com.soongsil.CoffeeChat.entity.*;
import com.soongsil.CoffeeChat.enums.ApplicationStatus;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import jakarta.annotation.PreDestroy;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ApplicationService {

	private final EntityManager em;
	private final ApplicationRepository applicationRepository;
	private final MentorRepository mentorRepository;
	private final MenteeRepository menteeRepository;
	private final UserRepository userRepository;
	private final PossibleDateRepository possibleDateRepository;
	private final EmailUtil emailUtil;
	private final ThreadPoolTaskExecutor executor;

	public ApplicationService(EntityManager em,
							  ApplicationRepository applicationRepository,
							  MentorRepository mentorRepository,
							  MenteeRepository menteeRepository,
							  UserRepository userRepository,
							  PossibleDateRepository possibleDateRepository,
							  EmailUtil emailUtil,
							  @Qualifier("performanceExecutor") ThreadPoolTaskExecutor executor) {
		this.em = em;
		this.applicationRepository = applicationRepository;
		this.mentorRepository = mentorRepository;
		this.menteeRepository = menteeRepository;
		this.userRepository = userRepository;
		this.possibleDateRepository = possibleDateRepository;
		this.emailUtil = emailUtil;
		this.executor = executor;
	}

	@Autowired
	private ApplicationContext applicationContext; // 프록시를 통해 자신을 호출하기 위해 ApplicationContext 주입

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	public PerformanceResult executePerformanceTest(int apiNum, PerformanceRequest request, ApplicationCreateRequest dto, String username) {
		int userCount = request.getUserCount();
		int totalRequests = request.getTotalRequests();
		System.out.println("totalRequests = " + totalRequests);

		List<Future<List<Long>>> futures = new ArrayList<>();

		for (int i = 0; i < userCount; i++) {
			futures.add(executor.submit(() -> {
				List<Long> executionTimes = new ArrayList<>();

				for (int j = 0; j < totalRequests; j++) {
					long startTime = System.currentTimeMillis();
					switch(apiNum){
						case 1: createApplicationWithJpaPerformance(dto, username); break;
						case 2: createApplicationPerformance(dto, username); break;
					}
					System.out.println("apiNum = " + apiNum);

					long endTime = System.currentTimeMillis();
					executionTimes.add(endTime - startTime);  // 각 요청의 실행 시간 기록
				}

				return executionTimes;
			}));
		}

		List<Double> averageTimesPerThread = new ArrayList<>();
		List<Long> allExecutionTimes = new ArrayList<>();

		// 대기 중인 모든 스레드가 완료될 때까지 기다림
		for (Future<List<Long>> future : futures) {
			try {
				List<Long> executionTimes = future.get();
				allExecutionTimes.addAll(executionTimes);

				// 스레드별 평균 실행 시간 계산
				double averageTimePerThread = executionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
				averageTimesPerThread.add(averageTimePerThread);

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		// 전체 스레드의 평균 실행 시간 계산
		double overallAverageTime = allExecutionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

		return new PerformanceResult(averageTimesPerThread, overallAverageTime);
	}

	@PreDestroy
	public void shutdownExecutor() {
		executor.shutdown();
		try {
			if (!executor.getThreadPoolExecutor().awaitTermination(60, TimeUnit.SECONDS)) {
				executor.getThreadPoolExecutor().shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.getThreadPoolExecutor().shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	@Transactional
	public ApplicationCreateResponse createApplicationPerformance(ApplicationCreateRequest request, String userName) throws Exception{
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
			//possibleDate.setActive(false);
			//possibleDateRepository.save(possibleDate);
		} else {
			throw new Exception("NOT FOUND");
		}

		Application savedApplication = request.toEntity(findMentor, findMentee);
		return ApplicationCreateResponse.from(savedApplication);
	}

	private ApplicationCreateResponse createApplicationWithJpaPerformance(ApplicationCreateRequest request, String userName) throws Exception{
		Mentor findMentor = mentorRepository.findById(request.getMentorId()).get();
		User findMentorUser = userRepository.findByMentor(findMentor);
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
			//possibleDate.setActive(false);
			//possibleDateRepository.save(possibleDate);
		} else {
			throw new Exception("NOT FOUND");
		}

		Application savedApplication = request.toEntity(findMentor, findMentee);

		return ApplicationCreateResponse.from(savedApplication);
	}

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

	@Transactional
	public ApplicationCreateResponse createApplicationWithJPA(ApplicationCreateRequest request, String userName) throws Exception {
		System.out.println("여긴들어옴");
		String lockKey = "lock:" + request.getMentorId() + ":" +request.getDate()+":"+ request.getStartTime();
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		boolean isLockAcquired = valueOperations.setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS);
		if (!isLockAcquired) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Lock을 획득하지 못하였습니다.");  //409반환
		}

		try {
			System.out.println("mentorid: " + request.getMentorId() + ", " + request.getDate() + ", " + request.getStartTime() + ", " + request.getEndTime());
			Mentor findMentor = mentorRepository.findById(request.getMentorId()).get();
			User findMentorUser = userRepository.findByMentor(findMentor);
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
