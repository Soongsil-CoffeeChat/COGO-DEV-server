package com.soongsil.CoffeeChat.service;

import com.soongsil.CoffeeChat.controller.exception.CustomException;
import com.soongsil.CoffeeChat.dto.ApplicationCreateRequestDto;
import com.soongsil.CoffeeChat.dto.ApplicationCreateResponseDto;
import com.soongsil.CoffeeChat.dto.ApplicationGetResponseDto;
import com.soongsil.CoffeeChat.dto.ApplicationMatchResponseDto;
import com.soongsil.CoffeeChat.entity.*;
import com.soongsil.CoffeeChat.enums.ApplicationStatus;
import com.soongsil.CoffeeChat.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;
import com.soongsil.CoffeeChat.util.email.EmailUtil;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soongsil.CoffeeChat.controller.exception.enums.ApplicationErrorCode.APPLICATION_NOT_FOUND;
import static com.soongsil.CoffeeChat.controller.exception.enums.ApplicationErrorCode.INVALID_MATCH_STATUS;
import static com.soongsil.CoffeeChat.controller.exception.enums.MentorErrorCode.MENTOR_NOT_FOUND;
import static com.soongsil.CoffeeChat.controller.exception.enums.PossibleDateErrorCode.POSSIBLE_DATE_NOT_FOUND;
import static com.soongsil.CoffeeChat.controller.exception.enums.PossibleDateErrorCode.PREEMPTED_POSSIBLE_DATE;
import static com.soongsil.CoffeeChat.controller.exception.enums.UserErrorCode.USER_NOT_FOUND;
import static com.soongsil.CoffeeChat.enums.ApplicationStatus.MATCHED;
import static com.soongsil.CoffeeChat.enums.ApplicationStatus.UNMATCHED;

@Service
@RequiredArgsConstructor
@Slf4j

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

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(
                        USER_NOT_FOUND.getHttpStatusCode(),
                        USER_NOT_FOUND.getErrorMessage())
                );
    }

    @Transactional
    public ApplicationCreateResponseDto createApplication(ApplicationCreateRequestDto request, String userName) throws
            Exception {
        // 		System.out.println("여긴들어옴");
        // 		String lockKey = "lock:" + request.getMentorId() + ":" +request.getDate()+":"+ request.getStartTime();
        // 		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        //
        // 		boolean isLockAcquired = valueOperations.setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS);
        // 		if (!isLockAcquired) {
        // 			throw new ResponseStatusException(HttpStatus.CONFLICT, "Lock을 획득하지 못하였습니다.");  //409반환
        // 		}
        //
        // 		try {
        // 			System.out.println("mentorid: " + request.getMentorId() + ", " + request.getDate() + ", " + request.getStartTime() + ", " + request.getEndTime());
        // 			User findMentorUser = userRepository.findByMentorIdWithFetch(request.getMentorId());
        // 			Mentor findMentor = findMentorUser.getMentor();
        // 			User findMenteeUser = userRepository.findByUsername(userName);
        // 			Mentee findMentee = findMenteeUser.getMentee();
        //
        // 			LocalTime startTime = request.getStartTime();
        // 			LocalDate date = request.getDate();
        //
        // 			// possibleDate 불러오는 JPQL
        // 			TypedQuery<PossibleDate> query = em.createQuery(
        // 					"SELECT p FROM PossibleDate p JOIN p.mentor m WHERE m.id = :mentorId AND p.startTime = :startTime AND p.date = :date",
        // 					PossibleDate.class);
        // 			query.setParameter("mentorId", request.getMentorId());
        // 			query.setParameter("startTime", startTime);
        // 			query.setParameter("date", date);
        //
        // 			Optional<PossibleDate> possibleDateOpt = query.getResultList().stream().findFirst();
        //
        // 			if (possibleDateOpt.isPresent()) {
        // 				PossibleDate possibleDate = possibleDateOpt.get();
        // 				if (!possibleDate.isActive()) {
        // 					throw new ResponseStatusException(HttpStatus.GONE, "이미 신청된 시간입니다.");  //410 반환
        // 				}
        // 				System.out.println("possibleDate.getId() = " + possibleDate.getId());
        // 				possibleDate.setActive(false);
        // 				possibleDateRepository.save(possibleDate);
        // 			} else {
        // 				throw new Exception("NOT FOUND");
        // 			}
        //
        // 			Application savedApplication = applicationRepository.save(request.toEntity(findMentor, findMentee));
        // /*
        // 			ApplicationService proxy = applicationContext.getBean(ApplicationService.class);
        // 			proxy.sendApplicationMatchedEmailAsync(findMenteeUser.getEmail(), findMentorUser.getName(),
        // 					findMenteeUser.getName(), savedApplication.getDate(), savedApplication.getStartTime(),
        // 					savedApplication.getEndTime());
        //
        //
        //  */
        // 			return ApplicationCreateResponse.from(savedApplication);
        // 		} finally {
        // 			redisTemplate.delete(lockKey);
        // 		}
        //TODO: Fetch Join
        // 가능시간 체크
        PossibleDate requestedPossibleDate = possibleDateRepository.findById(request.getPossibleDateId())
                .orElseThrow(() -> new CustomException(
                        POSSIBLE_DATE_NOT_FOUND.getHttpStatusCode(),
                        POSSIBLE_DATE_NOT_FOUND.getErrorMessage())
                );
        log.info("[*] Find possibleDate id: " + requestedPossibleDate.getId());

        // 선점된 가능시간
        if (!requestedPossibleDate.isActive()) {
            log.warn("[*] Found possibleDate(id:" + requestedPossibleDate.getId() + ") is already preempted");
            throw new CustomException(
                    PREEMPTED_POSSIBLE_DATE.getHttpStatusCode(),
                    PREEMPTED_POSSIBLE_DATE.getErrorMessage()
            );
        }
        log.info("[*] Found possibleDate is not preempted");

        // COGO 저장
        User user = findUserByUsername(userName);
        Mentee findMentee = user.getMentee();
        Mentor findMentor = mentorRepository.findById(request.getMentorId())
                .orElseThrow(() -> new CustomException(
                        MENTOR_NOT_FOUND.getHttpStatusCode(),
                        MENTOR_NOT_FOUND.getErrorMessage())
                );
        return ApplicationCreateResponseDto.from(
                applicationRepository.save(
                        request.toEntity(findMentor, findMentee, request.getMemo(), requestedPossibleDate))
        );
    }

    @Async("mailExecutor")
    public void sendApplicationMatchedEmailAsync(String email, String mentorName, String menteeName, LocalDate date,
                                                 LocalTime startTime, LocalTime endTime) throws MessagingException {
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

                        .accept(UNMATCHED)
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

    public ApplicationGetResponseDto getApplication(Long applicationId) {
        Application findApplication = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(
                        APPLICATION_NOT_FOUND.getHttpStatusCode(),
                        APPLICATION_NOT_FOUND.getErrorMessage()
                ));
        User findMenteeUser = userRepository.findByMenteeId(findApplication.getMentee().getId());
        User findMentorUser = userRepository.findByMentor(findApplication.getMentor());
        //TODO: toDTO 빌더 만들어두고, join으로 묶자
        return ApplicationGetResponseDto.builder()
                .applicationId(applicationId)
                .menteeName(findMenteeUser.getName())
                .mentorName(findMentorUser.getName())
                .memo(findApplication.getMemo())
                .date(findApplication.getPossibleDate().getDate())
                .startTime(findApplication.getPossibleDate().getStartTime())
                .endTime(findApplication.getPossibleDate().getEndTime())
                .build();
    }

    public List<ApplicationGetResponseDto> getApplications(String username, String applicationStatus) {
        if (!"matched".equalsIgnoreCase(applicationStatus) && !"unmatched".equalsIgnoreCase(applicationStatus)) {
            log.warn("[*] Requested applicationStatus is not MATCHED or UNMATCHED");
            throw new CustomException(
                    INVALID_MATCH_STATUS.getHttpStatusCode(),
                    INVALID_MATCH_STATUS.getErrorMessage()
            );
        }
        log.info("[*] Find applications with condition [" + applicationStatus + "]");

        //TODO: JOIN문으로 변경
        List<ApplicationGetResponseDto> dtos = new ArrayList<>();
        User user = findUserByUsername(username);
        List<Application> findApplications;

        findApplications = user.isMentor() ?
                applicationRepository.findApplicationByMentor(user.getMentor()) :
                user.isMentee() ?
                        applicationRepository.findApplicationByMentee(user.getMentee()) :
                        Collections.emptyList();

        for (Application app : findApplications) {
            if (app.getAccept() == ApplicationStatus.valueOf(applicationStatus.toUpperCase())) {
                User findMenteeUser = userRepository.findByMenteeId(app.getMentee().getId());
                // MATCHED든 UNMATCHED든 둘 중 하나 필터링 된 것들 다 반환
                dtos.add(ApplicationGetResponseDto.toDto(
                        app,
                        user.getName(),
                        findMenteeUser.getName()
                ));
            }
        }
        return dtos;
    }

    @Transactional
    public ApplicationMatchResponseDto updateApplicationStatus(Long applicationId, String decision) {
        Application findApplication = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(
                        APPLICATION_NOT_FOUND.getHttpStatusCode(),
                        APPLICATION_NOT_FOUND.getErrorMessage())
                );

        ApplicationMatchResponseDto responseDto = ApplicationMatchResponseDto.builder()
                .applicationId(applicationId)
                .build();
        PossibleDate matchedPossibleDate = findApplication.getPossibleDate();

        // 가능시간 비활성화
        System.out.println("possibleDate.getId() = " + matchedPossibleDate.getId());
        matchedPossibleDate.setActive(false);
        possibleDateRepository.save(matchedPossibleDate);
        log.info(
                "[*] PossibleDate(id:" + matchedPossibleDate.getId() + ") is just matched with: "
                        + findApplication.getId()
        );

        switch (decision) {
            case "reject" -> {
                log.warn("[*] Application({}) is deleted", applicationId);
                applicationRepository.deleteById(applicationId);
                responseDto.setStatus("REJECTED");
            }
            case "accept" -> {
                findApplication.setAccept(MATCHED);
                responseDto.setStatus(MATCHED.name());

                // 매치된 가능시간(PossibleDate)을 가진 다른 'UNMATCHED' 상태의 Application 엔티티 삭제
                List<Application> unmatchedApplications = applicationRepository
                        .findByPossibleDateAndAccept(matchedPossibleDate, ApplicationStatus.UNMATCHED);

                unmatchedApplications.forEach(application -> {
//                    application.setAccept(UNMATCHED);
                    applicationRepository.delete(application);
                    log.info("[*] Unmatched Application(id:" + application.getId() + ") with PossibleDate(id:"
                            + matchedPossibleDate.getId() + ") has been UNMATCHED");
                });
            }
            default -> throw new CustomException(
                    INVALID_MATCH_STATUS.getHttpStatusCode(),
                    INVALID_MATCH_STATUS.getErrorMessage()
            );
        }

        return responseDto;
    }

}
