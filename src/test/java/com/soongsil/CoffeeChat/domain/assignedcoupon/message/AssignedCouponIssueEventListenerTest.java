package com.soongsil.CoffeeChat.domain.assignedcoupon.message;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soongsil.CoffeeChat.infra.aws.s3.service.AmazonS3Service;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssignedCouponIssueEventListener 테스트")
class AssignedCouponIssueEventListenerTest {

    @Mock private AmazonS3Service amazonS3Service;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private AssignedCouponIssueEventListener listener;

    @Test
    @DisplayName("이벤트 수신 시 JSON 직렬화 후 event-logs/assigned-coupon/ 경로에 업로드")
    void handleEvent_uploadsToCorrectS3Path() throws Exception {
        // given
        AssignedCouponIssuedEvent event =
                new AssignedCouponIssuedEvent(
                        "test_user_001",
                        "가나다",
                        "01011112222",
                        "AC-0001",
                        LocalDateTime.of(2025, 5, 14, 10, 0));

        String expectedJson =
                "{\"username\":\"test_user_001\",\"name\":\"가나다\",\"phoneNum\":\"01011112222\","
                        + "\"couponNumber\":\"AC-0001\",\"issuedAt\":\"2025-05-14T10:00:00\"}";

        given(objectMapper.writeValueAsString(event)).willReturn(expectedJson);
        given(amazonS3Service.uploadJsonFile(anyString(), anyString(), anyString()))
                .willReturn(
                        "https://cloudfront.example.com/event-logs/assigned-coupon/assigned-coupon-AC-0001_xxx.json");

        // when
        listener.handleAssignedCouponIssuedEvent(event);

        // then
        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> dirCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> prefixCaptor = ArgumentCaptor.forClass(String.class);

        verify(amazonS3Service)
                .uploadJsonFile(jsonCaptor.capture(), dirCaptor.capture(), prefixCaptor.capture());

        assertThat(jsonCaptor.getValue()).isEqualTo(expectedJson);
        assertThat(dirCaptor.getValue()).isEqualTo("event-logs/assigned-coupon");
        assertThat(prefixCaptor.getValue()).isEqualTo("assigned-coupon-AC-0001");
    }

    @Test
    @DisplayName("JSON 직렬화 실패해도 예외를 외부로 던지지 않음 (로깅만)")
    void handleEvent_swallowsSerializationException() throws Exception {
        // given
        AssignedCouponIssuedEvent event =
                new AssignedCouponIssuedEvent(
                        "u1", "n", "01000000000", "AC-0001", LocalDateTime.now());
        given(objectMapper.writeValueAsString(event))
                .willThrow(new RuntimeException("serialize failed"));

        // when & then
        assertThatCode(() -> listener.handleAssignedCouponIssuedEvent(event))
                .doesNotThrowAnyException();

        verifyNoInteractions(amazonS3Service);
    }

    @Test
    @DisplayName("S3 업로드 실패해도 예외를 외부로 던지지 않음 (로깅만)")
    void handleEvent_swallowsS3UploadException() throws Exception {
        // given
        AssignedCouponIssuedEvent event =
                new AssignedCouponIssuedEvent(
                        "u1", "n", "01000000000", "AC-0001", LocalDateTime.now());
        given(objectMapper.writeValueAsString(event)).willReturn("{}");
        given(amazonS3Service.uploadJsonFile(anyString(), anyString(), anyString()))
                .willThrow(new RuntimeException("S3 error"));

        // when & then
        assertThatCode(() -> listener.handleAssignedCouponIssuedEvent(event))
                .doesNotThrowAnyException();
    }
}
