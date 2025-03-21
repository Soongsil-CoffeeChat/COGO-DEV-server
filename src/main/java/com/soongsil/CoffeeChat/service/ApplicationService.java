package com.soongsil.CoffeeChat.service;

import static com.soongsil.CoffeeChat.entity.enums.ApplicationStatus.MATCHED;
import static com.soongsil.CoffeeChat.entity.enums.ApplicationStatus.UNMATCHED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.dto.ApplicationConverter;
import com.soongsil.CoffeeChat.dto.ApplicationRequest.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.dto.ApplicationResponse.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.dto.ApplicationResponse.ApplicationGetResponse;
import com.soongsil.CoffeeChat.dto.ApplicationResponse.ApplicationMatchResponse;
import com.soongsil.CoffeeChat.entity.*;
import com.soongsil.CoffeeChat.entity.enums.ApplicationStatus;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.repository.MenteeRepository;
import com.soongsil.CoffeeChat.repository.Mentor.MentorRepository;
import com.soongsil.CoffeeChat.repository.PossibleDate.PossibleDateRepository;
import com.soongsil.CoffeeChat.repository.User.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    //    private final EmailUtil emailUtil;

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public ApplicationCreateResponse createApplication(
            ApplicationCreateRequest request, String userName) {
        PossibleDate requestedPossibleDate =
                possibleDateRepository
                        .findById(request.getPossibleDateId())
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.POSSIBLE_DATE_NOT_FOUND));
        log.info("[*] Find possibleDate id: " + requestedPossibleDate.getId());

        // 선점된 가능시간
        if (!requestedPossibleDate.isActive()) {
            log.warn(
                    "[*] Found possibleDate(id:"
                            + requestedPossibleDate.getId()
                            + ") is already preempted");
            throw new GlobalException(GlobalErrorCode.PREEMPTED_POSSIBLE_DATE);
        }
        log.info("[*] Found possibleDate is not preempted");

        // COGO 저장
        User user = findUserByUsername(userName);
        Mentee findMentee = user.getMentee();
        Mentor findMentor =
                mentorRepository
                        .findById(request.getMentorId())
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.MENTOR_NOT_FOUND));

        Application application =
                ApplicationConverter.toEntity(
                        findMentor, findMentee, request.getMemo(), requestedPossibleDate);
        applicationRepository.save(application);
        return ApplicationConverter.toResponse(application);
    }

    // 동시성 테스트용
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
    private static final AtomicInteger transactionCounter = new AtomicInteger(0); // 트랜잭션마다 ID부여

    @Transactional
    public Application createApplicationIfPossible(
            Long possibleDateId, Mentor mentor, Mentee mentee) throws Exception {
        int transactionId = transactionCounter.incrementAndGet(); // 트랜잭션 ID 1씩 증가하며 부여
        MDC.put("transactionId", String.valueOf(transactionId)); // 로그에 트랜잭션ID 띄우기
        MDC.put("threadId", String.valueOf(Thread.currentThread().getId())); // 로그에 스레드ID 띄우기

        try {
            logger.info("aaa트랜잭션 시작");

            PossibleDate possibleDate = em.find(PossibleDate.class, possibleDateId);

            if (possibleDate != null && possibleDate.isActive()) { // Active상태면, Application생성
                possibleDate.setActive(false); // 중요! active상태를 false로 변경
                em.merge(possibleDate);

                Application application =
                        Application.builder()
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

    @Transactional(readOnly = true)
    public ApplicationGetResponse getApplication(Long applicationId) {
        Application findApplication =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND));
        User findMenteeUser = userRepository.findByMenteeId(findApplication.getMentee().getId());
        User findMentorUser = userRepository.findByMentor(findApplication.getMentor());
        // TODO: toDTO 빌더 만들어두고, join으로 묶자
        return ApplicationConverter.toGetResponse(
                findApplication, findMentorUser.getName(), findMenteeUser.getName());
    }

    @Transactional(readOnly = true)
    public List<ApplicationGetResponse> getApplications(String username, String applicationStatus) {
        if (!"matched".equalsIgnoreCase(applicationStatus)
                && !"unmatched".equalsIgnoreCase(applicationStatus)) {
            log.warn("[*] Requested applicationStatus is not MATCHED or UNMATCHED");
            throw new GlobalException(GlobalErrorCode.APPLICATION_INVALID_MATCH_STATUS);
        }
        log.info("[*] Find applications with condition [" + applicationStatus + "]");

        // TODO: JOIN문으로 변경
        List<ApplicationGetResponse> dtos = new ArrayList<>();
        User user = findUserByUsername(username);
        List<Application> findApplications;

        findApplications =
                user.isMentor()
                        ? applicationRepository.findApplicationByMentor(user.getMentor())
                        : user.isMentee()
                                ? applicationRepository.findApplicationByMentee(user.getMentee())
                                : Collections.emptyList();

        for (Application app : findApplications) {
            if (app.getAccept() == ApplicationStatus.valueOf(applicationStatus.toUpperCase())) {
                User findMenteeUser = userRepository.findByMenteeId(app.getMentee().getId());
                // MATCHED든 UNMATCHED든 둘 중 하나 필터링 된 것들 다 반환
                dtos.add(
                        ApplicationConverter.toGetResponse(
                                app, user.getName(), findMenteeUser.getName()));
            }
        }
        return dtos;
    }

    @Transactional
    public ApplicationMatchResponse updateApplicationStatus(Long applicationId, String decision) {
        Application findApplication =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND));

        ApplicationMatchResponse responseDto = ApplicationConverter.toResponse(applicationId);
        PossibleDate matchedPossibleDate = findApplication.getPossibleDate();

        // 가능시간 비활성화
        System.out.println("possibleDate.getId() = " + matchedPossibleDate.getId());
        matchedPossibleDate.setActive(false);
        possibleDateRepository.save(matchedPossibleDate);
        log.info(
                "[*] PossibleDate(id:"
                        + matchedPossibleDate.getId()
                        + ") is just matched with: "
                        + findApplication.getId());

        switch (decision) {
            case "reject" -> {
                log.warn("[*] Application({}) is deleted", applicationId);
                applicationRepository.deleteById(applicationId);
                responseDto.setApplicationStatus("REJECTED");
            }
            case "accept" -> {
                findApplication.setAccept(MATCHED);
                responseDto.setApplicationStatus(MATCHED.name());

                // 매치된 가능시간(PossibleDate)을 가진 다른 'UNMATCHED' 상태의 Application 엔티티 삭제
                List<Application> unmatchedApplications =
                        applicationRepository.findByPossibleDateAndAccept(
                                matchedPossibleDate, ApplicationStatus.UNMATCHED);

                unmatchedApplications.forEach(
                        application -> {
                            //                    application.setAccept(UNMATCHED);
                            applicationRepository.delete(application);
                            log.info(
                                    "[*] Unmatched Application(id:"
                                            + application.getId()
                                            + ") with PossibleDate(id:"
                                            + matchedPossibleDate.getId()
                                            + ") has been UNMATCHED");
                        });
            }
            default -> throw new GlobalException(GlobalErrorCode.APPLICATION_INVALID_MATCH_STATUS);
        }

        return responseDto;
    }
}
