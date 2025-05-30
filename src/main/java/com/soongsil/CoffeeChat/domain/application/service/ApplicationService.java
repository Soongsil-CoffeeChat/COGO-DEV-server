package com.soongsil.CoffeeChat.domain.application.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.application.dto.ApplicationConverter;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationRequest.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationGetResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationMatchResponse;
import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.enums.ApplicationStatus;
import com.soongsil.CoffeeChat.domain.application.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.domain.mentee.entity.Mentee;
import com.soongsil.CoffeeChat.domain.mentor.entity.Mentor;
import com.soongsil.CoffeeChat.domain.mentor.repository.MentorRepository;
import com.soongsil.CoffeeChat.domain.possibleDate.entity.PossibleDate;
import com.soongsil.CoffeeChat.domain.possibleDate.repository.PossibleDateRepository;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;
import com.soongsil.CoffeeChat.infra.sms.SmsUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final EntityManager em;
    private final ApplicationRepository applicationRepository;
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;
    private final PossibleDateRepository possibleDateRepository;
    private final SmsUtil smsUtil;

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    private Application findApplicationById(Long applicationId) {
        return applicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND));
    }

    @Transactional
    public ApplicationCreateResponse createApplication(
            ApplicationCreateRequest request, String userName) {
        PossibleDate requestedPossibleDate =
                possibleDateRepository
                        .findById(request.getPossibleDateId())
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.POSSIBLE_DATE_NOT_FOUND));

        // 선점된 가능시간
        if (!requestedPossibleDate.isActive()) {
            throw new GlobalException(GlobalErrorCode.PREEMPTED_POSSIBLE_DATE);
        }

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
        smsUtil.sendMentorNotificationMessage(application);
        return ApplicationConverter.toResponse(application);
    }

    @Transactional(readOnly = true)
    public ApplicationGetResponse getApplication(Long applicationId) {
        Application findApplication = findApplicationById(applicationId);
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
        Application application = findApplicationById(applicationId);
        application.getPossibleDate().deactivate();
        smsUtil.sendMenteeNotificationMessage(application);
        switch (decision) {
            case "reject" -> {
                application.rejectApplication();
                return ApplicationConverter.toResponse(
                        applicationId, application.getAccept().name());
            }
            case "accept" -> {
                application.acceptApplication();
                rejectUnmatchedApplications(application);
                return ApplicationConverter.toResponse(
                        applicationId, application.getAccept().name());
            }
            default -> throw new GlobalException(GlobalErrorCode.APPLICATION_INVALID_MATCH_STATUS);
        }
    }

    private void rejectUnmatchedApplications(Application application) {
        List<Application> unmatchedApplications =
                applicationRepository.findByPossibleDateAndAccept(
                        application.getPossibleDate(), ApplicationStatus.UNMATCHED);
        unmatchedApplications.forEach(Application::rejectApplication);
        unmatchedApplications.forEach(smsUtil::sendMenteeNotificationMessage);
    }
}
