package com.soongsil.CoffeeChat.domain.application.service;

import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.application.dto.ApplicationConverter;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationRequest.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationGetResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationMatchResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationSummaryResponse;
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
        Long findMentee = findApplication.getMentee().getId();
        Long findMentor = findApplication.getMentor().getId();
        return ApplicationConverter.toGetResponse(findApplication, findMentee, findMentor);
    }

    @Transactional
    public List<ApplicationSummaryResponse> getApplications(
            String userName, ApplicationStatus status) {
        User User =
                userRepository
                        .findByUsername(userName)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        List<Application> applications =
                applicationRepository.findByUserNameAndOptionalStatus(userName, status);

        return applications.stream()
                .map(
                        application -> {
                            User otherPartyUser = getOtherParty(application, userName);
                            return ApplicationConverter.toSummaryResponse(
                                    application, otherPartyUser.getName());
                        })
                .toList();
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

    public User getOtherParty(Application application, String userName) {
        User user =
                userRepository
                        .findByUsername(userName)
                        .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        if (user.isMentee()) return application.getMentor().getUser();
        else return application.getMentee().getUser();
    }
}
