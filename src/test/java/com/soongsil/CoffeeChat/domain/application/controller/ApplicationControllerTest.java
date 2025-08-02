package com.soongsil.CoffeeChat.domain.application.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationRequest.ApplicationCreateRequest;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationCreateResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationGetResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationResponse.ApplicationMatchResponse;
import com.soongsil.CoffeeChat.domain.application.dto.ApplicationSummaryResponse;
import com.soongsil.CoffeeChat.domain.application.service.ApplicationService;
import com.soongsil.CoffeeChat.global.security.oauth2.CustomOAuth2User;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ApplicationController.class)
@DisplayName("ApplicationController 단위 테스트")
class ApplicationControllerTest {

    @MockBean
    private ApplicationService applicationService;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomOAuth2User customOAuth2User;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ApplicationController applicationController = new ApplicationController(applicationService);
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("createApplication 메서드 테스트")
    class CreateApplicationTest {

        @Test
        @DisplayName("성공: 유효한 요청으로 애플리케이션 생성")
        @WithMockUser
        void createApplication_Success() throws Exception {
            // Given
            ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                    .content("테스트 내용")
                    .mentorId(1L)
                    .build();

            ApplicationCreateResponse response = ApplicationCreateResponse.builder()
                    .applicationId(1L)
                    .content("테스트 내용")
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            when(authentication.getPrincipal()).thenReturn(customOAuth2User);
            when(customOAuth2User.getUsername()).thenReturn("testuser@test.com");
            when(applicationService.createApplication(any(ApplicationCreateRequest.class), eq("testuser@test.com")))
                    .thenReturn(response);

            // When & Then
            mockMvc.perform(post("/api/v1/applications")
                            .with(authentication(authentication))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/applications/1"))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value("CREATED"))
                    .andExpect(jsonPath("$.result.applicationId").value(1))
                    .andExpect(jsonPath("$.result.content").value("테스트 내용"))
                    .andExpect(jsonPath("$.result.status").value("PENDING"));

            verify(applicationService).createApplication(any(ApplicationCreateRequest.class), eq("testuser@test.com"));
        }

        @Test
        @DisplayName("실패: 인증되지 않은 사용자")
        void createApplication_Unauthorized() throws Exception {
            // Given
            ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                    .content("테스트 내용")
                    .mentorId(1L)
                    .build();

            // When & Then
            mockMvc.perform(post("/api/v1/applications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(applicationService, never()).createApplication(any(), any());
        }

        @Test
        @DisplayName("실패: 잘못된 요청 본문")
        void createApplication_BadRequest() throws Exception {
            // Given
            String invalidJson = "{ invalid json }";

            when(authentication.getPrincipal()).thenReturn(customOAuth2User);
            when(customOAuth2User.getUsername()).thenReturn("testuser@test.com");

            // When & Then
            mockMvc.perform(post("/api/v1/applications")
                            .with(authentication(authentication))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(applicationService, never()).createApplication(any(), any());
        }

        @Test
        @DisplayName("실패: 빈 요청 본문")
        void createApplication_EmptyBody() throws Exception {
            // Given
            when(authentication.getPrincipal()).thenReturn(customOAuth2User);
            when(customOAuth2User.getUsername()).thenReturn("testuser@test.com");

            // When & Then
            mockMvc.perform(post("/api/v1/applications")
                            .with(authentication(authentication))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk()); // Will be caught by service layer validation

            verify(applicationService).createApplication(any(ApplicationCreateRequest.class), eq("testuser@test.com"));
        }

        @Test
        @DisplayName("실패: 서비스 계층에서 예외 발생")
        void createApplication_ServiceException() throws Exception {
            // Given
            ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                    .content("테스트 내용")
                    .mentorId(1L)
                    .build();

            when(authentication.getPrincipal()).thenReturn(customOAuth2User);
            when(customOAuth2User.getUsername()).thenReturn("testuser@test.com");
            when(applicationService.createApplication(any(ApplicationCreateRequest.class), eq("testuser@test.com")))
                    .thenThrow(new RuntimeException("서비스 에러"));

            // When & Then
            mockMvc.perform(post("/api/v1/applications")
                            .with(authentication(authentication))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).createApplication(any(ApplicationCreateRequest.class), eq("testuser@test.com"));
        }
    }

    @Nested
    @DisplayName("getApplication 메서드 테스트")
    class GetApplicationTest {

        @Test
        @DisplayName("성공: 유효한 ID로 애플리케이션 조회")
        void getApplication_Success() throws Exception {
            // Given
            Long applicationId = 1L;
            ApplicationGetResponse response = ApplicationGetResponse.builder()
                    .applicationId(applicationId)
                    .content("테스트 내용")
                    .status("APPROVED")
                    .applicantName("신청자")
                    .mentorName("멘토")
                    .createdAt(LocalDateTime.now())
                    .build();

            when(applicationService.getApplication(applicationId)).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/api/v1/applications/{applicationId}", applicationId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value("OK"))
                    .andExpected(jsonPath("$.result.applicationId").value(1))
                    .andExpect(jsonPath("$.result.content").value("테스트 내용"))
                    .andExpect(jsonPath("$.result.status").value("APPROVED"))
                    .andExpect(jsonPath("$.result.applicantName").value("신청자"))
                    .andExpect(jsonPath("$.result.mentorName").value("멘토"));

            verify(applicationService).getApplication(applicationId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 애플리케이션 ID")
        void getApplication_NotFound() throws Exception {
            // Given
            Long nonExistentId = 999L;
            when(applicationService.getApplication(nonExistentId))
                    .thenThrow(new RuntimeException("애플리케이션을 찾을 수 없습니다."));

            // When & Then
            mockMvc.perform(get("/api/v1/applications/{applicationId}", nonExistentId))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).getApplication(nonExistentId);
        }

        @Test
        @DisplayName("실패: 잘못된 ID 형식")
        void getApplication_InvalidIdFormat() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/applications/{applicationId}", "invalid"))
                    .andExpect(status().isBadRequest());

            verify(applicationService, never()).getApplication(any());
        }

        @Test
        @DisplayName("경계값: 음수 ID")
        void getApplication_NegativeId() throws Exception {
            // Given
            Long negativeId = -1L;
            when(applicationService.getApplication(negativeId))
                    .thenThrow(new IllegalArgumentException("유효하지 않은 ID입니다."));

            // When & Then
            mockMvc.perform(get("/api/v1/applications/{applicationId}", negativeId))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).getApplication(negativeId);
        }

        @Test
        @DisplayName("경계값: 0 ID")
        void getApplication_ZeroId() throws Exception {
            // Given
            Long zeroId = 0L;
            when(applicationService.getApplication(zeroId))
                    .thenThrow(new IllegalArgumentException("유효하지 않은 ID입니다."));

            // When & Then
            mockMvc.perform(get("/api/v1/applications/{applicationId}", zeroId))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).getApplication(zeroId);
        }
    }

    @Nested
    @DisplayName("getApplications 메서드 테스트")
    class GetApplicationsTest {

        @Test
        @DisplayName("성공: 사용자의 애플리케이션 목록 조회")
        @WithMockUser
        void getApplications_Success() throws Exception {
            // Given
            List<ApplicationSummaryResponse> responses = Arrays.asList(
                    ApplicationSummaryResponse.builder()
                            .applicationId(1L)
                            .applicantName("신청자1")
                            .status("PENDING")
                            .createdAt(LocalDateTime.now())
                            .build(),
                    ApplicationSummaryResponse.builder()
                            .applicationId(2L)
                            .applicantName("신청자2")
                            .status("APPROVED")
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            when(authentication.getPrincipal()).thenReturn(customOAuth2User);
            when(customOAuth2User.getUsername()).thenReturn("testuser@test.com");
            when(applicationService.getApplications("testuser@test.com")).thenReturn(responses);

            // When & Then
            mockMvc.perform(get("/api/v1/applications/applications")
                            .with(authentication(authentication)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value("OK"))
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.length()").value(2))
                    .andExpect(jsonPath("$.result[0].applicationId").value(1))
                    .andExpect(jsonPath("$.result[0].applicantName").value("신청자1"))
                    .andExpect(jsonPath("$.result[0].status").value("PENDING"))
                    .andExpect(jsonPath("$.result[1].applicationId").value(2))
                    .andExpect(jsonPath("$.result[1].applicantName").value("신청자2"))
                    .andExpect(jsonPath("$.result[1].status").value("APPROVED"));

            verify(applicationService).getApplications("testuser@test.com");
        }

        @Test
        @DisplayName("성공: 빈 애플리케이션 목록")
        @WithMockUser
        void getApplications_EmptyList() throws Exception {
            // Given
            when(authentication.getPrincipal()).thenReturn(customOAuth2User);
            when(customOAuth2User.getUsername()).thenReturn("testuser@test.com");
            when(applicationService.getApplications("testuser@test.com")).thenReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/v1/applications/applications")
                            .with(authentication(authentication)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value("OK"))
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.length()").value(0));

            verify(applicationService).getApplications("testuser@test.com");
        }

        @Test
        @DisplayName("실패: 인증되지 않은 사용자")
        void getApplications_Unauthorized() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/applications/applications"))
                    .andExpect(status().isUnauthorized());

            verify(applicationService, never()).getApplications(any());
        }

        @Test
        @DisplayName("실패: 서비스 계층에서 예외 발생")
        @WithMockUser
        void getApplications_ServiceException() throws Exception {
            // Given
            when(authentication.getPrincipal()).thenReturn(customOAuth2User);
            when(customOAuth2User.getUsername()).thenReturn("testuser@test.com");
            when(applicationService.getApplications("testuser@test.com"))
                    .thenThrow(new RuntimeException("서비스 에러"));

            // When & Then
            mockMvc.perform(get("/api/v1/applications/applications")
                            .with(authentication(authentication)))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).getApplications("testuser@test.com");
        }
    }

    @Nested
    @DisplayName("updateApplicationStatus 메서드 테스트")
    class UpdateApplicationStatusTest {

        @Test
        @DisplayName("성공: 애플리케이션 승인")
        void updateApplicationStatus_Approve_Success() throws Exception {
            // Given
            Long applicationId = 1L;
            String decision = "APPROVED";
            ApplicationMatchResponse response = ApplicationMatchResponse.builder()
                    .applicationId(applicationId)
                    .status("APPROVED")
                    .matchedAt(LocalDateTime.now())
                    .build();

            when(applicationService.updateApplicationStatus(applicationId, decision)).thenReturn(response);

            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", applicationId)
                            .param("decision", decision))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value("OK"))
                    .andExpect(jsonPath("$.result.applicationId").value(1))
                    .andExpect(jsonPath("$.result.status").value("APPROVED"));

            verify(applicationService).updateApplicationStatus(applicationId, decision);
        }

        @Test
        @DisplayName("성공: 애플리케이션 거절")
        void updateApplicationStatus_Reject_Success() throws Exception {
            // Given
            Long applicationId = 1L;
            String decision = "REJECTED";
            ApplicationMatchResponse response = ApplicationMatchResponse.builder()
                    .applicationId(applicationId)
                    .status("REJECTED")
                    .rejectedAt(LocalDateTime.now())
                    .build();

            when(applicationService.updateApplicationStatus(applicationId, decision)).thenReturn(response);

            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", applicationId)
                            .param("decision", decision))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.code").value("OK"))
                    .andExpect(jsonPath("$.result.applicationId").value(1))
                    .andExpect(jsonPath("$.result.status").value("REJECTED"));

            verify(applicationService).updateApplicationStatus(applicationId, decision);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 애플리케이션 ID")
        void updateApplicationStatus_NotFound() throws Exception {
            // Given
            Long nonExistentId = 999L;
            String decision = "APPROVED";
            when(applicationService.updateApplicationStatus(nonExistentId, decision))
                    .thenThrow(new RuntimeException("애플리케이션을 찾을 수 없습니다."));

            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", nonExistentId)
                            .param("decision", decision))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).updateApplicationStatus(nonExistentId, decision);
        }

        @Test
        @DisplayName("실패: 잘못된 decision 값")
        void updateApplicationStatus_InvalidDecision() throws Exception {
            // Given
            Long applicationId = 1L;
            String invalidDecision = "INVALID_STATUS";
            when(applicationService.updateApplicationStatus(applicationId, invalidDecision))
                    .thenThrow(new IllegalArgumentException("유효하지 않은 결정 값입니다."));

            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", applicationId)
                            .param("decision", invalidDecision))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).updateApplicationStatus(applicationId, invalidDecision);
        }

        @Test
        @DisplayName("실패: decision 파라미터 누락")
        void updateApplicationStatus_MissingDecision() throws Exception {
            // Given
            Long applicationId = 1L;

            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", applicationId))
                    .andExpect(status().isBadRequest());

            verify(applicationService, never()).updateApplicationStatus(any(), any());
        }

        @Test
        @DisplayName("실패: 빈 decision 값")
        void updateApplicationStatus_EmptyDecision() throws Exception {
            // Given
            Long applicationId = 1L;
            String emptyDecision = "";
            when(applicationService.updateApplicationStatus(applicationId, emptyDecision))
                    .thenThrow(new IllegalArgumentException("결정 값이 비어있습니다."));

            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", applicationId)
                            .param("decision", emptyDecision))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).updateApplicationStatus(applicationId, emptyDecision);
        }

        @Test
        @DisplayName("실패: 잘못된 ID 형식")
        void updateApplicationStatus_InvalidIdFormat() throws Exception {
            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", "invalid")
                            .param("decision", "APPROVED"))
                    .andExpect(status().isBadRequest());

            verify(applicationService, never()).updateApplicationStatus(any(), any());
        }

        @Test
        @DisplayName("경계값: 음수 ID")
        void updateApplicationStatus_NegativeId() throws Exception {
            // Given
            Long negativeId = -1L;
            String decision = "APPROVED";
            when(applicationService.updateApplicationStatus(negativeId, decision))
                    .thenThrow(new IllegalArgumentException("유효하지 않은 ID입니다."));

            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", negativeId)
                            .param("decision", decision))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).updateApplicationStatus(negativeId, decision);
        }

        @Test
        @DisplayName("경계값: 대소문자 혼합 decision")
        void updateApplicationStatus_MixedCaseDecision() throws Exception {
            // Given
            Long applicationId = 1L;
            String mixedCaseDecision = "approved";
            ApplicationMatchResponse response = ApplicationMatchResponse.builder()
                    .applicationId(applicationId)
                    .status("APPROVED")
                    .matchedAt(LocalDateTime.now())
                    .build();

            when(applicationService.updateApplicationStatus(applicationId, mixedCaseDecision)).thenReturn(response);

            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", applicationId)
                            .param("decision", mixedCaseDecision))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.applicationId").value(1));

            verify(applicationService).updateApplicationStatus(applicationId, mixedCaseDecision);
        }

        @Test
        @DisplayName("경계값: 매우 긴 decision 문자열")
        void updateApplicationStatus_VeryLongDecision() throws Exception {
            // Given
            Long applicationId = 1L;
            String longDecision = "A".repeat(1000);
            when(applicationService.updateApplicationStatus(applicationId, longDecision))
                    .thenThrow(new IllegalArgumentException("결정 값이 너무 깁니다."));

            // When & Then
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", applicationId)
                            .param("decision", longDecision))
                    .andExpect(status().isInternalServerError());

            verify(applicationService).updateApplicationStatus(applicationId, longDecision);
        }
    }

    @Nested
    @DisplayName("통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("시나리오: 애플리케이션 생성부터 상태 업데이트까지")
        @WithMockUser
        void fullApplicationLifecycle() throws Exception {
            // Given
            ApplicationCreateRequest createRequest = ApplicationCreateRequest.builder()
                    .content("멘토링 신청합니다")
                    .mentorId(1L)
                    .build();

            ApplicationCreateResponse createResponse = ApplicationCreateResponse.builder()
                    .applicationId(1L)
                    .content("멘토링 신청합니다")
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            ApplicationGetResponse getResponse = ApplicationGetResponse.builder()
                    .applicationId(1L)
                    .content("멘토링 신청합니다")
                    .status("PENDING")
                    .applicantName("신청자")
                    .mentorName("멘토")
                    .createdAt(LocalDateTime.now())
                    .build();

            ApplicationMatchResponse matchResponse = ApplicationMatchResponse.builder()
                    .applicationId(1L)
                    .status("APPROVED")
                    .matchedAt(LocalDateTime.now())
                    .build();

            when(authentication.getPrincipal()).thenReturn(customOAuth2User);
            when(customOAuth2User.getUsername()).thenReturn("testuser@test.com");
            when(applicationService.createApplication(any(ApplicationCreateRequest.class), eq("testuser@test.com")))
                    .thenReturn(createResponse);
            when(applicationService.getApplication(1L)).thenReturn(getResponse);
            when(applicationService.updateApplicationStatus(1L, "APPROVED")).thenReturn(matchResponse);

            // When & Then - 1. 애플리케이션 생성
            mockMvc.perform(post("/api/v1/applications")
                            .with(authentication(authentication))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.result.applicationId").value(1))
                    .andExpect(jsonPath("$.result.status").value("PENDING"));

            // When & Then - 2. 애플리케이션 조회
            mockMvc.perform(get("/api/v1/applications/{applicationId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.applicationId").value(1))
                    .andExpect(jsonPath("$.result.status").value("PENDING"));

            // When & Then - 3. 애플리케이션 승인
            mockMvc.perform(patch("/api/v1/applications/{applicationId}/decision", 1L)
                            .param("decision", "APPROVED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.applicationId").value(1))
                    .andExpect(jsonPath("$.result.status").value("APPROVED"));

            // Verify all service calls
            verify(applicationService).createApplication(any(ApplicationCreateRequest.class), eq("testuser@test.com"));
            verify(applicationService).getApplication(1L);
            verify(applicationService).updateApplicationStatus(1L, "APPROVED");
        }
    }
}