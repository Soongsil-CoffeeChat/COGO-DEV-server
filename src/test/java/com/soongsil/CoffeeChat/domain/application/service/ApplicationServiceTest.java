package com.soongsil.CoffeeChat.domain.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

/**
 * ApplicationService 단위 테스트
 * 
 * 이 테스트는 MockitoExtension을 사용하여 의존성을 모킹하고
 * Spring Boot의 기본 테스트 의존성(JUnit 5, Mockito, AssertJ)을 활용합니다.
 * 
 * 테스트 범위:
 * - createApplication: 신청서 생성 기능 (성공/실패 시나리오)
 * - getApplication: 개별 신청서 조회 기능
 * - getApplications: 신청서 목록 조회 기능 (멘토/멘티 구분)
 * - updateApplicationStatus: 신청서 상태 업데이트 (수락/거절)
 * - 각종 예외 상황 및 엣지 케이스
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationService 단위 테스트")
class ApplicationServiceTest {

    @Mock
    private EntityManager em;
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private MentorRepository mentorRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PossibleDateRepository possibleDateRepository;
    
    @Mock
    private SmsUtil smsUtil;

    @InjectMocks
    private ApplicationService applicationService;

    private User testUser;
    private Mentee testMentee;
    private Mentor testMentor;
    private PossibleDate testPossibleDate;
    private Application testApplication;
    private ApplicationCreateRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        testUser = mock(User.class);
        testMentee = mock(Mentee.class);
        testMentor = mock(Mentor.class);
        testPossibleDate = mock(PossibleDate.class);
        testApplication = mock(Application.class);
        
        testCreateRequest = ApplicationCreateRequest.builder()
                .mentorId(1L)
                .possibleDateId(1L)
                .memo("Test memo")
                .build();

        // 기본 mock 동작 설정
        when(testUser.getMentee()).thenReturn(testMentee);
        when(testPossibleDate.isActive()).thenReturn(true);
        when(testApplication.getId()).thenReturn(1L);
        when(testApplication.getMentee()).thenReturn(testMentee);
        when(testApplication.getMentor()).thenReturn(testMentor);
        when(testApplication.getPossibleDate()).thenReturn(testPossibleDate);
        when(testApplication.getAccept()).thenReturn(ApplicationStatus.UNMATCHED);
    }

    @Nested
    @DisplayName("createApplication 메서드 테스트")
    class CreateApplicationTests {

        @Test
        @DisplayName("정상적인 신청서 생성 - 모든 필수 조건이 만족될 때 성공")
        void createApplication_Success() {
            // Given
            String username = "testuser";
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
            when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

            // When
            ApplicationCreateResponse result = applicationService.createApplication(testCreateRequest, username);

            // Then
            assertThat(result).isNotNull();
            verify(applicationRepository).save(any(Application.class));
            verify(smsUtil).sendMentorNotificationMessage(any(Application.class));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 - USER_NOT_FOUND 예외 발생")
        void createApplication_UserNotFound_ThrowsException() {
            // Given
            String username = "nonexistent";
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.createApplication(testCreateRequest, username))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("존재하지 않는 가능 날짜 - POSSIBLE_DATE_NOT_FOUND 예외 발생")
        void createApplication_PossibleDateNotFound_ThrowsException() {
            // Given
            String username = "testuser";
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.createApplication(testCreateRequest, username))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.POSSIBLE_DATE_NOT_FOUND);
        }

        @Test
        @DisplayName("비활성화된 가능 날짜 - PREEMPTED_POSSIBLE_DATE 예외 발생")
        void createApplication_InactivePossibleDate_ThrowsException() {
            // Given
            String username = "testuser";
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(testPossibleDate.isActive()).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> applicationService.createApplication(testCreateRequest, username))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.PREEMPTED_POSSIBLE_DATE);
        }

        @Test
        @DisplayName("존재하지 않는 멘토 - MENTOR_NOT_FOUND 예외 발생")
        void createApplication_MentorNotFound_ThrowsException() {
            // Given
            String username = "testuser";
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.createApplication(testCreateRequest, username))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.MENTOR_NOT_FOUND);
        }

        @Test
        @DisplayName("빈 메모로 신청서 생성 - 정상 처리")
        void createApplication_EmptyMemo_Success() {
            // Given
            String username = "testuser";
            ApplicationCreateRequest requestWithEmptyMemo = ApplicationCreateRequest.builder()
                    .mentorId(1L)
                    .possibleDateId(1L)
                    .memo("")
                    .build();
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
            when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

            // When
            ApplicationCreateResponse result = applicationService.createApplication(requestWithEmptyMemo, username);

            // Then
            assertThat(result).isNotNull();
            verify(applicationRepository).save(any(Application.class));
        }

        @Test
        @DisplayName("null 메모로 신청서 생성 - 정상 처리")
        void createApplication_NullMemo_Success() {
            // Given
            String username = "testuser";
            ApplicationCreateRequest requestWithNullMemo = ApplicationCreateRequest.builder()
                    .mentorId(1L)
                    .possibleDateId(1L)
                    .memo(null)
                    .build();
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
            when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

            // When
            ApplicationCreateResponse result = applicationService.createApplication(requestWithNullMemo, username);

            // Then
            assertThat(result).isNotNull();
            verify(applicationRepository).save(any(Application.class));
        }

        @Test
        @DisplayName("음수 멘토 ID - MENTOR_NOT_FOUND 예외 발생")
        void createApplication_NegativeMentorId_ThrowsException() {
            // Given
            String username = "testuser";
            ApplicationCreateRequest requestWithNegativeMentorId = ApplicationCreateRequest.builder()
                    .mentorId(-1L)
                    .possibleDateId(1L)
                    .memo("Test memo")
                    .build();
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(-1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.createApplication(requestWithNegativeMentorId, username))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.MENTOR_NOT_FOUND);
        }

        @Test
        @DisplayName("0인 가능 날짜 ID - POSSIBLE_DATE_NOT_FOUND 예외 발생")
        void createApplication_ZeroPossibleDateId_ThrowsException() {
            // Given
            String username = "testuser";
            ApplicationCreateRequest requestWithZeroPossibleDateId = ApplicationCreateRequest.builder()
                    .mentorId(1L)
                    .possibleDateId(0L)
                    .memo("Test memo")
                    .build();
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(0L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.createApplication(requestWithZeroPossibleDateId, username))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.POSSIBLE_DATE_NOT_FOUND);
        }

        @Test
        @DisplayName("매우 긴 메모로 신청서 생성 - 정상 처리")
        void createApplication_VeryLongMemo_Success() {
            // Given
            String username = "testuser";
            String longMemo = "A".repeat(1000);
            ApplicationCreateRequest requestWithLongMemo = ApplicationCreateRequest.builder()
                    .mentorId(1L)
                    .possibleDateId(1L)
                    .memo(longMemo)
                    .build();
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
            when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

            // When
            ApplicationCreateResponse result = applicationService.createApplication(requestWithLongMemo, username);

            // Then
            assertThat(result).isNotNull();
            verify(applicationRepository).save(any(Application.class));
        }

        @Test
        @DisplayName("특수 문자가 포함된 메모로 신청서 생성 - 정상 처리")
        void createApplication_MemoWithSpecialCharacters_Success() {
            // Given
            String username = "testuser";
            String specialMemo = "Hello! @#$%^&*()_+ 안녕하세요 🎉";
            ApplicationCreateRequest requestWithSpecialMemo = ApplicationCreateRequest.builder()
                    .mentorId(1L)
                    .possibleDateId(1L)
                    .memo(specialMemo)
                    .build();
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
            when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

            // When
            ApplicationCreateResponse result = applicationService.createApplication(requestWithSpecialMemo, username);

            // Then
            assertThat(result).isNotNull();
            verify(applicationRepository).save(any(Application.class));
        }
    }

    @Nested
    @DisplayName("getApplication 메서드 테스트")
    class GetApplicationTests {

        @Test
        @DisplayName("정상적인 신청서 조회 - 성공")
        void getApplication_Success() {
            // Given
            Long applicationId = 1L;
            User menteeUser = mock(User.class);
            User mentorUser = mock(User.class);
            
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));
            when(userRepository.findByMenteeId(any())).thenReturn(menteeUser);
            when(userRepository.findByMentor(any())).thenReturn(mentorUser);
            when(menteeUser.getName()).thenReturn("Mentee Name");
            when(mentorUser.getName()).thenReturn("Mentor Name");

            // When
            ApplicationGetResponse result = applicationService.getApplication(applicationId);

            // Then
            assertThat(result).isNotNull();
            verify(applicationRepository).findById(applicationId);
            verify(userRepository).findByMenteeId(any());
            verify(userRepository).findByMentor(any());
        }

        @Test
        @DisplayName("존재하지 않는 신청서 ID - APPLICATION_NOT_FOUND 예외 발생")
        void getApplication_NotFound_ThrowsException() {
            // Given
            Long nonExistentId = 999L;
            when(applicationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.getApplication(nonExistentId))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.APPLICATION_NOT_FOUND);
        }

        @Test
        @DisplayName("null 신청서 ID - 예외 발생")
        void getApplication_NullId_ThrowsException() {
            // Given
            Long nullId = null;

            // When & Then
            assertThatThrownBy(() -> applicationService.getApplication(nullId))
                    .isInstanceOf(GlobalException.class);
        }

        @Test
        @DisplayName("음수 신청서 ID - APPLICATION_NOT_FOUND 예외 발생")
        void getApplication_NegativeId_ThrowsException() {
            // Given
            Long negativeId = -1L;
            when(applicationRepository.findById(negativeId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.getApplication(negativeId))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.APPLICATION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("getApplications 메서드 테스트")
    class GetApplicationsTests {

        @Test
        @DisplayName("멘토 사용자의 신청서 목록 조회 - 성공")
        void getApplications_MentorUser_Success() {
            // Given
            String username = "mentor";
            User mentorUser = mock(User.class);
            User otherUser = mock(User.class);
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(mentorUser));
            when(mentorUser.isMentor()).thenReturn(true);
            when(mentorUser.getMentor()).thenReturn(testMentor);
            when(applicationRepository.findApplicationByMentor(testMentor)).thenReturn(List.of(testApplication));
            
            when(testMentor.getUser()).thenReturn(mentorUser);
            when(testMentee.getUser()).thenReturn(otherUser);
            when(mentorUser.getName()).thenReturn(username);
            when(otherUser.getName()).thenReturn("Other User");
            when(testPossibleDate.getDate()).thenReturn(LocalDateTime.now());

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(username);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getOtherPartyName()).isEqualTo("Other User");
            verify(applicationRepository).findApplicationByMentor(testMentor);
        }

        @Test
        @DisplayName("멘티 사용자의 신청서 목록 조회 - 성공")
        void getApplications_MenteeUser_Success() {
            // Given
            String username = "mentee";
            User menteeUser = mock(User.class);
            User otherUser = mock(User.class);
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(menteeUser));
            when(menteeUser.isMentor()).thenReturn(false);
            when(menteeUser.isMentee()).thenReturn(true);
            when(menteeUser.getMentee()).thenReturn(testMentee);
            when(applicationRepository.findApplicationByMentee(testMentee)).thenReturn(List.of(testApplication));
            
            when(testMentor.getUser()).thenReturn(otherUser);
            when(testMentee.getUser()).thenReturn(menteeUser);
            when(menteeUser.getName()).thenReturn(username);
            when(otherUser.getName()).thenReturn("Other User");
            when(testPossibleDate.getDate()).thenReturn(LocalDateTime.now());

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(username);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getOtherPartyName()).isEqualTo("Other User");
            verify(applicationRepository).findApplicationByMentee(testMentee);
        }

        @Test
        @DisplayName("멘토도 멘티도 아닌 사용자 - 빈 목록 반환")
        void getApplications_NeitherMentorNorMentee_ReturnsEmptyList() {
            // Given
            String username = "regular_user";
            User regularUser = mock(User.class);
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(regularUser));
            when(regularUser.isMentor()).thenReturn(false);
            when(regularUser.isMentee()).thenReturn(false);

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(username);

            // Then
            assertThat(result).isEmpty();
            verify(applicationRepository, never()).findApplicationByMentor(any());
            verify(applicationRepository, never()).findApplicationByMentee(any());
        }

        @Test
        @DisplayName("존재하지 않는 사용자 - USER_NOT_FOUND 예외 발생")
        void getApplications_UserNotFound_ThrowsException() {
            // Given
            String username = "nonexistent";
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.getApplications(username))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("신청서 목록 날짜 내림차순 정렬 - 최신 순으로 정렬")
        void getApplications_SortsApplicationsByDateDescending() {
            // Given
            String username = "mentor";
            User mentorUser = mock(User.class);
            User otherUser = mock(User.class);
            
            Application app1 = mock(Application.class);
            Application app2 = mock(Application.class);
            PossibleDate date1 = mock(PossibleDate.class);
            PossibleDate date2 = mock(PossibleDate.class);
            
            LocalDateTime earlierDate = LocalDateTime.now().minusDays(1);
            LocalDateTime laterDate = LocalDateTime.now();
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(mentorUser));
            when(mentorUser.isMentor()).thenReturn(true);
            when(mentorUser.getMentor()).thenReturn(testMentor);
            when(applicationRepository.findApplicationByMentor(testMentor)).thenReturn(List.of(app1, app2));
            
            // Setup app1 (earlier date)
            when(app1.getId()).thenReturn(1L);
            when(app1.getMentor()).thenReturn(testMentor);
            when(app1.getMentee()).thenReturn(testMentee);
            when(app1.getPossibleDate()).thenReturn(date1);
            when(app1.getAccept()).thenReturn(ApplicationStatus.UNMATCHED);
            when(date1.getDate()).thenReturn(earlierDate);
            
            // Setup app2 (later date)
            when(app2.getId()).thenReturn(2L);
            when(app2.getMentor()).thenReturn(testMentor);
            when(app2.getMentee()).thenReturn(testMentee);
            when(app2.getPossibleDate()).thenReturn(date2);
            when(app2.getAccept()).thenReturn(ApplicationStatus.UNMATCHED);
            when(date2.getDate()).thenReturn(laterDate);
            
            when(testMentor.getUser()).thenReturn(mentorUser);
            when(testMentee.getUser()).thenReturn(otherUser);
            when(mentorUser.getName()).thenReturn(username);
            when(otherUser.getName()).thenReturn("Other User");

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(username);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getApplicationDate()).isEqualTo(laterDate);
            assertThat(result.get(1).getApplicationDate()).isEqualTo(earlierDate);
        }

        @Test
        @DisplayName("MATCHED 상태 - '수락'으로 표시")
        void getApplications_MatchedStatus_ShowsAsAccepted() {
            // Given
            String username = "mentor";
            User mentorUser = mock(User.class);
            User otherUser = mock(User.class);
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(mentorUser));
            when(mentorUser.isMentor()).thenReturn(true);
            when(mentorUser.getMentor()).thenReturn(testMentor);
            when(applicationRepository.findApplicationByMentor(testMentor)).thenReturn(List.of(testApplication));
            
            when(testApplication.getAccept()).thenReturn(ApplicationStatus.MATCHED);
            when(testMentor.getUser()).thenReturn(mentorUser);
            when(testMentee.getUser()).thenReturn(otherUser);
            when(mentorUser.getName()).thenReturn(username);
            when(otherUser.getName()).thenReturn("Other User");
            when(testPossibleDate.getDate()).thenReturn(LocalDateTime.now());

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(username);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getApplicationStatus()).isEqualTo("수락");
        }

        @Test
        @DisplayName("MATCHED 이외의 상태 - '거절'로 표시")
        void getApplications_NonMatchedStatus_ShowsAsRejected() {
            // Given
            String username = "mentor";
            User mentorUser = mock(User.class);
            User otherUser = mock(User.class);
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(mentorUser));
            when(mentorUser.isMentor()).thenReturn(true);
            when(mentorUser.getMentor()).thenReturn(testMentor);
            when(applicationRepository.findApplicationByMentor(testMentor)).thenReturn(List.of(testApplication));
            
            when(testApplication.getAccept()).thenReturn(ApplicationStatus.UNMATCHED);
            when(testMentor.getUser()).thenReturn(mentorUser);
            when(testMentee.getUser()).thenReturn(otherUser);
            when(mentorUser.getName()).thenReturn(username);
            when(otherUser.getName()).thenReturn("Other User");
            when(testPossibleDate.getDate()).thenReturn(LocalDateTime.now());

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(username);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getApplicationStatus()).isEqualTo("거절");
        }
    }

    @Nested
    @DisplayName("updateApplicationStatus 메서드 테스트")
    class UpdateApplicationStatusTests {

        @Test
        @DisplayName("신청서 거절 - 정상 처리")
        void updateApplicationStatus_Reject_Success() {
            // Given
            Long applicationId = 1L;
            String decision = "reject";
            
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));

            // When
            ApplicationMatchResponse result = applicationService.updateApplicationStatus(applicationId, decision);

            // Then
            assertThat(result).isNotNull();
            verify(testApplication).rejectApplication();
            verify(testPossibleDate).deactivate();
            verify(smsUtil).sendMenteeNotificationMessage(testApplication);
        }

        @Test
        @DisplayName("신청서 수락 - 정상 처리 및 다른 신청서 거절")
        void updateApplicationStatus_Accept_Success() {
            // Given
            Long applicationId = 1L;
            String decision = "accept";
            List<Application> unmatchedApps = List.of(mock(Application.class));
            
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));
            when(applicationRepository.findByPossibleDateAndAccept(testPossibleDate, ApplicationStatus.UNMATCHED))
                    .thenReturn(unmatchedApps);

            // When
            ApplicationMatchResponse result = applicationService.updateApplicationStatus(applicationId, decision);

            // Then
            assertThat(result).isNotNull();
            verify(testApplication).acceptApplication();
            verify(testPossibleDate).deactivate();
            verify(smsUtil).sendMenteeNotificationMessage(testApplication);
            verify(applicationRepository).findByPossibleDateAndAccept(testPossibleDate, ApplicationStatus.UNMATCHED);
        }

        @Test
        @DisplayName("잘못된 결정 값 - APPLICATION_INVALID_MATCH_STATUS 예외 발생")
        void updateApplicationStatus_InvalidDecision_ThrowsException() {
            // Given
            Long applicationId = 1L;
            String invalidDecision = "invalid";
            
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));

            // When & Then
            assertThatThrownBy(() -> applicationService.updateApplicationStatus(applicationId, invalidDecision))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.APPLICATION_INVALID_MATCH_STATUS);
        }

        @Test
        @DisplayName("존재하지 않는 신청서 ID - APPLICATION_NOT_FOUND 예외 발생")
        void updateApplicationStatus_ApplicationNotFound_ThrowsException() {
            // Given
            Long nonExistentId = 999L;
            String decision = "accept";
            
            when(applicationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.updateApplicationStatus(nonExistentId, decision))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.APPLICATION_NOT_FOUND);
        }

        @Test
        @DisplayName("수락 시 연관된 미매칭 신청서들 거절 처리")
        void updateApplicationStatus_Accept_RejectsUnmatchedApplications() {
            // Given
            Long applicationId = 1L;
            String decision = "accept";
            Application unmatchedApp1 = mock(Application.class);
            Application unmatchedApp2 = mock(Application.class);
            List<Application> unmatchedApps = List.of(unmatchedApp1, unmatchedApp2);
            
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));
            when(applicationRepository.findByPossibleDateAndAccept(testPossibleDate, ApplicationStatus.UNMATCHED))
                    .thenReturn(unmatchedApps);

            // When
            applicationService.updateApplicationStatus(applicationId, decision);

            // Then
            verify(unmatchedApp1).rejectApplication();
            verify(unmatchedApp2).rejectApplication();
            verify(smsUtil).sendMenteeNotificationMessage(unmatchedApp1);
            verify(smsUtil).sendMenteeNotificationMessage(unmatchedApp2);
        }

        @Test
        @DisplayName("수락 시 연관된 미매칭 신청서가 없는 경우 - 정상 처리")
        void updateApplicationStatus_Accept_NoUnmatchedApplications_Success() {
            // Given
            Long applicationId = 1L;
            String decision = "accept";
            
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));
            when(applicationRepository.findByPossibleDateAndAccept(testPossibleDate, ApplicationStatus.UNMATCHED))
                    .thenReturn(Collections.emptyList());

            // When
            ApplicationMatchResponse result = applicationService.updateApplicationStatus(applicationId, decision);

            // Then
            assertThat(result).isNotNull();
            verify(testApplication).acceptApplication();
            verify(testPossibleDate).deactivate();
            verify(smsUtil).sendMenteeNotificationMessage(testApplication);
        }

        @Test
        @DisplayName("대소문자 구분 - 정확한 소문자만 허용")
        void updateApplicationStatus_CaseSensitive() {
            // Given
            Long applicationId = 1L;
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));

            // When & Then - 대문자는 예외 발생
            assertThatThrownBy(() -> applicationService.updateApplicationStatus(applicationId, "ACCEPT"))
                    .isInstanceOf(GlobalException.class);
            
            assertThatThrownBy(() -> applicationService.updateApplicationStatus(applicationId, "REJECT"))
                    .isInstanceOf(GlobalException.class);
            
            // 정확한 소문자는 정상 처리
            applicationService.updateApplicationStatus(applicationId, "accept");
            verify(testApplication).acceptApplication();
        }
    }

    @Nested
    @DisplayName("Private 메서드 간접 테스트")
    class PrivateMethodTests {

        @Test
        @DisplayName("findUserByUsername - 사용자를 찾을 수 없을 때 예외 발생")
        void findUserByUsername_NotFound_ThroughCreateApplication() {
            // Given
            String username = "nonexistent";
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.createApplication(testCreateRequest, username))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("findApplicationById - 신청서를 찾을 수 없을 때 예외 발생")
        void findApplicationById_NotFound_ThroughGetApplication() {
            // Given
            Long applicationId = 999L;
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> applicationService.getApplication(applicationId))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue("errorCode", GlobalErrorCode.APPLICATION_NOT_FOUND);
        }

        @Test
        @DisplayName("rejectUnmatchedApplications - 빈 목록에서도 정상 동작")
        void rejectUnmatchedApplications_EmptyList_ThroughAccept() {
            // Given
            Long applicationId = 1L;
            String decision = "accept";
            
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));
            when(applicationRepository.findByPossibleDateAndAccept(testPossibleDate, ApplicationStatus.UNMATCHED))
                    .thenReturn(Collections.emptyList());

            // When
            ApplicationMatchResponse result = applicationService.updateApplicationStatus(applicationId, decision);

            // Then
            assertThat(result).isNotNull();
            verify(applicationRepository).findByPossibleDateAndAccept(testPossibleDate, ApplicationStatus.UNMATCHED);
        }
    }

    @Nested
    @DisplayName("엣지 케이스 및 예외 상황 테스트")
    class EdgeCaseTests {

        @Test
        @DisplayName("SMS 전송 실패 시에도 메인 로직은 정상 수행")
        void smsFailure_DoesNotAffectMainLogic() {
            // Given
            String username = "testuser";
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
            when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);
            doThrow(new RuntimeException("SMS service unavailable")).when(smsUtil).sendMentorNotificationMessage(any());

            // When & Then
            assertThatThrownBy(() -> applicationService.createApplication(testCreateRequest, username))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("SMS service unavailable");
            
            // 저장은 여전히 호출되었는지 확인
            verify(applicationRepository).save(any(Application.class));
        }

        @Test
        @DisplayName("EntityManager가 사용되지 않음을 확인")
        void entityManager_NotUsed() {
            // Given
            String username = "testuser";
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
            when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

            // When
            applicationService.createApplication(testCreateRequest, username);

            // Then
            verifyNoInteractions(em);
        }

        @Test
        @DisplayName("동일한 가능 날짜에 대한 중복 신청서 처리")
        void multipleApplications_SamePossibleDate() {
            // Given
            Long applicationId = 1L;
            String decision = "accept";
            Application duplicateApp = mock(Application.class);
            List<Application> unmatchedApps = List.of(duplicateApp);
            
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));
            when(applicationRepository.findByPossibleDateAndAccept(testPossibleDate, ApplicationStatus.UNMATCHED))
                    .thenReturn(unmatchedApps);

            // When
            applicationService.updateApplicationStatus(applicationId, decision);

            // Then
            verify(duplicateApp).rejectApplication();
            verify(smsUtil).sendMenteeNotificationMessage(duplicateApp);
        }
    }

    @Nested
    @DisplayName("Transaction 어노테이션 검증")
    class TransactionTests {

        @Test
        @DisplayName("createApplication은 @Transactional이 적용되어야 한다")
        void createApplication_IsTransactional() throws NoSuchMethodException {
            // Given
            var method = ApplicationService.class.getMethod("createApplication", ApplicationCreateRequest.class, String.class);
            
            // When & Then
            assertThat(method.isAnnotationPresent(org.springframework.transaction.annotation.Transactional.class)).isTrue();
        }

        @Test
        @DisplayName("getApplication은 readOnly 트랜잭션이 적용되어야 한다")
        void getApplication_IsReadOnlyTransactional() throws NoSuchMethodException {
            // Given
            var method = ApplicationService.class.getMethod("getApplication", Long.class);
            var transactional = method.getAnnotation(org.springframework.transaction.annotation.Transactional.class);
            
            // When & Then
            assertThat(transactional).isNotNull();
            assertThat(transactional.readOnly()).isTrue();
        }

        @Test
        @DisplayName("updateApplicationStatus는 @Transactional이 적용되어야 한다")
        void updateApplicationStatus_IsTransactional() throws NoSuchMethodException {
            // Given
            var method = ApplicationService.class.getMethod("updateApplicationStatus", Long.class, String.class);
            
            // When & Then
            assertThat(method.isAnnotationPresent(org.springframework.transaction.annotation.Transactional.class)).isTrue();
        }

        @Test
        @DisplayName("getApplications는 @Transactional이 적용되어야 한다")
        void getApplications_IsTransactional() throws NoSuchMethodException {
            // Given
            var method = ApplicationService.class.getMethod("getApplications", String.class);
            
            // When & Then
            assertThat(method.isAnnotationPresent(org.springframework.transaction.annotation.Transactional.class)).isTrue();
        }
    }

    @Nested
    @DisplayName("ApplicationStatus 열거형 처리 테스트")
    class ApplicationStatusTests {

        @Test
        @DisplayName("REJECTED 상태 처리 - '거절'로 표시")
        void handlesRejectedStatus() {
            // Given
            String username = "mentor";
            User mentorUser = mock(User.class);
            User otherUser = mock(User.class);
            
            Application rejectedApp = mock(Application.class);
            when(rejectedApp.getId()).thenReturn(1L);
            when(rejectedApp.getMentor()).thenReturn(testMentor);
            when(rejectedApp.getMentee()).thenReturn(testMentee);
            when(rejectedApp.getPossibleDate()).thenReturn(testPossibleDate);
            when(rejectedApp.getAccept()).thenReturn(ApplicationStatus.REJECTED);
            
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(mentorUser));
            when(mentorUser.isMentor()).thenReturn(true);
            when(mentorUser.getMentor()).thenReturn(testMentor);
            when(applicationRepository.findApplicationByMentor(testMentor)).thenReturn(List.of(rejectedApp));
            
            when(testMentor.getUser()).thenReturn(mentorUser);
            when(testMentee.getUser()).thenReturn(otherUser);
            when(mentorUser.getName()).thenReturn(username);
            when(otherUser.getName()).thenReturn("Other User");
            when(testPossibleDate.getDate()).thenReturn(LocalDateTime.now());

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(username);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getApplicationStatus()).isEqualTo("거절");
        }
    }

    @Nested
    @DisplayName("멘토와 멘티 역할 구분 테스트")
    class MentorMenteeRoleTests {

        @Test
        @DisplayName("멘토 사용자의 이름과 상대방 이름을 올바르게 구분")
        void correctlyDistinguishesMentorAndMenteeNames() {
            // Given
            String mentorUsername = "mentor_user";
            User mentorUser = mock(User.class);
            User menteeUser = mock(User.class);
            
            when(userRepository.findByUsername(mentorUsername)).thenReturn(Optional.of(mentorUser));
            when(mentorUser.isMentor()).thenReturn(true);
            when(mentorUser.getMentor()).thenReturn(testMentor);
            when(applicationRepository.findApplicationByMentor(testMentor)).thenReturn(List.of(testApplication));
            
            when(testMentor.getUser()).thenReturn(mentorUser);
            when(testMentee.getUser()).thenReturn(menteeUser);
            when(mentorUser.getName()).thenReturn(mentorUsername);
            when(menteeUser.getName()).thenReturn("mentee_user");
            when(testPossibleDate.getDate()).thenReturn(LocalDateTime.now());

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(mentorUsername);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getOtherPartyName()).isEqualTo("mentee_user");
        }

        @Test
        @DisplayName("멘티 사용자의 이름과 상대방 이름을 올바르게 구분")
        void correctlyDistinguishesMenteeAndMentorNames() {
            // Given
            String menteeUsername = "mentee_user";
            User menteeUser = mock(User.class);
            User mentorUser = mock(User.class);
            
            when(userRepository.findByUsername(menteeUsername)).thenReturn(Optional.of(menteeUser));
            when(menteeUser.isMentor()).thenReturn(false);
            when(menteeUser.isMentee()).thenReturn(true);
            when(menteeUser.getMentee()).thenReturn(testMentee);
            when(applicationRepository.findApplicationByMentee(testMentee)).thenReturn(List.of(testApplication));
            
            when(testMentor.getUser()).thenReturn(mentorUser);
            when(testMentee.getUser()).thenReturn(menteeUser);
            when(menteeUser.getName()).thenReturn(menteeUsername);
            when(mentorUser.getName()).thenReturn("mentor_user");
            when(testPossibleDate.getDate()).thenReturn(LocalDateTime.now());

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(menteeUsername);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getOtherPartyName()).isEqualTo("mentor_user");
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 검증 테스트")
    class BusinessLogicTests {

        @Test
        @DisplayName("신청서 생성 시 순서대로 검증")
        void createApplication_ValidatesInOrder() {
            // Given
            String username = "testuser";
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
            when(possibleDateRepository.findById(1L)).thenReturn(Optional.of(testPossibleDate));
            when(mentorRepository.findById(1L)).thenReturn(Optional.of(testMentor));
            when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

            // When
            applicationService.createApplication(testCreateRequest, username);

            // Then - 호출 순서 검증
            var inOrder = inOrder(userRepository, possibleDateRepository, testPossibleDate, mentorRepository, applicationRepository, smsUtil);
            inOrder.verify(userRepository).findByUsername(username);
            inOrder.verify(possibleDateRepository).findById(1L);
            inOrder.verify(testPossibleDate).isActive();
            inOrder.verify(mentorRepository).findById(1L);
            inOrder.verify(applicationRepository).save(any(Application.class));
            inOrder.verify(smsUtil).sendMentorNotificationMessage(any(Application.class));
        }

        @Test
        @DisplayName("신청서 상태 업데이트 시 가능 날짜 비활성화가 먼저 실행")
        void updateApplicationStatus_DeactivatesPossibleDateFirst() {
            // Given
            Long applicationId = 1L;
            String decision = "reject";
            when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(testApplication));

            // When
            applicationService.updateApplicationStatus(applicationId, decision);

            // Then - 가능 날짜 비활성화가 먼저 실행되는지 확인
            var inOrder = inOrder(testPossibleDate, testApplication, smsUtil);
            inOrder.verify(testPossibleDate).deactivate();
            inOrder.verify(testApplication).rejectApplication();
            inOrder.verify(smsUtil).sendMenteeNotificationMessage(testApplication);
        }

        @Test
        @DisplayName("이름 비교 로직 정확성 검증")
        void nameComparisonLogic_IsCorrect() {
            // Given
            String currentUsername = "current_user";
            User currentUser = mock(User.class);
            User otherUser = mock(User.class);
            
            when(userRepository.findByUsername(currentUsername)).thenReturn(Optional.of(currentUser));
            when(currentUser.isMentor()).thenReturn(true);
            when(currentUser.getMentor()).thenReturn(testMentor);
            when(applicationRepository.findApplicationByMentor(testMentor)).thenReturn(List.of(testApplication));
            
            when(testMentor.getUser()).thenReturn(currentUser);
            when(testMentee.getUser()).thenReturn(otherUser);
            when(currentUser.getName()).thenReturn(currentUsername);
            when(otherUser.getName()).thenReturn("other_user");
            when(testPossibleDate.getDate()).thenReturn(LocalDateTime.now());

            // When
            List<ApplicationSummaryResponse> result = applicationService.getApplications(currentUsername);

            // Then
            assertThat(result).isNotEmpty();
            assertThat(result.get(0).getOtherPartyName()).isEqualTo("other_user");
        }
    }
}