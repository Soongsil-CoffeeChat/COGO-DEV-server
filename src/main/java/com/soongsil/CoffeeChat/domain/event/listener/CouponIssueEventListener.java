package com.soongsil.CoffeeChat.domain.event.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soongsil.CoffeeChat.domain.event.dto.CouponIssuedEvent;
import com.soongsil.CoffeeChat.infra.aws.s3.service.AmazonS3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueEventListener {

    private final AmazonS3Service amazonS3Service;
    private final ObjectMapper objectMapper;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Async
    @EventListener
    public void handleCouponIssuedEvent(CouponIssuedEvent event) {
        try {
            // CouponIssuedEvent JSON 직렬화
            String logJson = objectMapper.writeValueAsString(event);

            String fileNamePrefix = "coupon-issue-" + event.applicationId();

            // event-logs 디렉토리에 저장
            String fileUrl = amazonS3Service.uploadJsonFile(logJson, "event-logs", fileNamePrefix);

            log.info("S3 발급 로그 업로드 완료: {} (URL: {})", fileNamePrefix, fileUrl);

        } catch (Exception e) {
            log.error("S3 발급 로그 업로드 실패. Application ID: {}", event.applicationId(), e);
        }
    }
}
