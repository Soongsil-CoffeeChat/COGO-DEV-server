package com.soongsil.CoffeeChat.domain.event.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.soongsil.CoffeeChat.domain.event.dto.EventCheckResponse;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.domain.event.dto.CouponIssuedEvent;
import com.soongsil.CoffeeChat.domain.event.dto.EventStatusResponse;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${event.store.pin}")
    private String storePin;

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    private boolean checkMenteeLimit(Long menteeId) {
        String userKey = "event:user:participation:" + menteeId;
        String countString = redisTemplate.opsForValue().get(userKey);
        int currentCount=(countString!=null)?Integer.parseInt(countString):0;

        return currentCount<2;
    }

    private boolean checkApplicationLimit(String applicationIdString){
        Boolean isAlreadyIssued = redisTemplate
                .opsForSet()
                .isMember("event:issued:applications", applicationIdString);
        return !Boolean.TRUE.equals(isAlreadyIssued);
    }


    // 이벤트 참여 이력 조회
    public EventCheckResponse checkEligibility(String username, Long applicationId){
        User user=findUserByUsername(username);

        // 해당 코고 이벤트 참여 이력 확인
        boolean issued=checkApplicationLimit(applicationId.toString());

        // 멘티 일 시 참여 이력 상한 확인 (2회)
        boolean limitExceeded;
        if(user.isMentee()){
            Long menteeId=user.getMentee().getId();
            limitExceeded=checkMenteeLimit(menteeId);
        } else {
            limitExceeded=false;
        }

        return EventCheckResponse.builder()
                .isAlreadyIssued(issued)
                .isLimitExceeded(limitExceeded)
                .canIssue(!issued&&!limitExceeded)
                .build();
    }

    // 멘토: QR 토큰 발급 로직
    @Transactional(readOnly = true)
    public String generateQrToken(Long applicationId, String username) {
        User user = findUserByUsername(username);

        if (!user.isMentor()) {
            throw new GlobalException(GlobalErrorCode.EVENT_NOT_MENTOR);
        }

        Application application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getMentor().getId().equals(user.getMentor().getId())) {
            throw new GlobalException(GlobalErrorCode.EVENT_NOT_YOUR_CHAT);
        }

        if(!checkApplicationLimit(applicationId.toString())){
            throw new GlobalException(GlobalErrorCode.EVENT_ALREADY_ISSUED);
        }

        if (application.getPossibleDate() == null) {
            throw new GlobalException(GlobalErrorCode.POSSIBLE_DATE_NOT_FOUND);
        }

        LocalDateTime applicationEndTime =
                LocalDateTime.of(
                        application.getPossibleDate().getDate(),
                        application.getPossibleDate().getEndTime());

        LocalDateTime expireTime = applicationEndTime.plusWeeks(1);

        long ttlSeconds = Duration.between(LocalDateTime.now(), expireTime).getSeconds();

        if (ttlSeconds <= 0) {
            throw new GlobalException(GlobalErrorCode.EVENT_APPLICATION_EXPIRED);
        }

        String token = UUID.randomUUID().toString();
        redisTemplate
                .opsForValue()
                .set(
                        "event:qr:token:" + token,
                        applicationId.toString(),
                        ttlSeconds,
                        TimeUnit.SECONDS);

        return token;
    }

    // 멘티: qr 인증 -> 매장 핀 번호 인증
    @Transactional(readOnly = true)
    public String verifyQrAndIssueCoupon(String qrToken, String inputPin, String username) {
        if (!storePin.equals(inputPin)) {
            throw new GlobalException(GlobalErrorCode.EVENT_PIN_MISMATCH);
        }

        // 1. 사용자 역할, 토큰 유효성 검증
        User user = findUserByUsername(username);

        if (!user.isMentee()) {
            throw new GlobalException(GlobalErrorCode.EVENT_NOT_MENTEE);
        }

        String tokenKey = "event:qr:token:" + qrToken;
        String applicationIdString = redisTemplate.opsForValue().get(tokenKey);

        if (applicationIdString == null) {
            throw new GlobalException(GlobalErrorCode.EVENT_QR_EXPIRED);
        }

        Long applicationId = Long.parseLong(applicationIdString);

        // 2. application 소유권 검즘
        Application application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getMentee().getId().equals(user.getMentee().getId())) {
            throw new IllegalStateException("본인이 참여한 커피챗의 QR 코드만 스캔할 수 있습니다.");
        }

        Long menteeId = user.getMentee().getId();
        Long mentorId = application.getMentor().getId();

        // 3. Redisson 분산 락 동시성 제어
        String lockKey = "lock:coupon:issue:" + applicationIdString;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(3, 3, TimeUnit.SECONDS)) {
                throw new GlobalException(GlobalErrorCode.EVENT_CONCURRENCY_ERROR);
            }

            // 1) 채팅방 1회 발급 제한 검증
            if(!checkApplicationLimit(applicationId.toString())){
                throw new GlobalException(GlobalErrorCode.EVENT_ALREADY_ISSUED);
            }
            // 2) 멘티 이벤트 참여 횟수 제한 검증
            if(!checkMenteeLimit(menteeId)){
                throw new GlobalException(GlobalErrorCode.EVENT_LIMIT_EXCEEDED);
            }

            // 3) 쿠폰 발급
            String couponUrl = redisTemplate.opsForList().leftPop("event:coupons");
            if (couponUrl == null) {
                throw new GlobalException(GlobalErrorCode.EVENT_COUPON_EXHAUSTED);
            }

            // 4) redis 상태 업데이트
            redisTemplate.opsForSet().add("event:issued:applications", applicationIdString);
            redisTemplate.opsForValue().increment("event:user:participation:" + menteeId);

            // 5) 사용된 토큰 파기
            redisTemplate.delete(tokenKey);

            // 6) 비동기 로그 저장 이벤트 발행
            eventPublisher.publishEvent(
                    new CouponIssuedEvent(
                            applicationId, menteeId, mentorId, couponUrl, LocalDateTime.now()));
            return couponUrl;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("동시성 처리 중 인터럽트가 발생했습니다.");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public EventStatusResponse getEventStatus() {

        Long size = redisTemplate.opsForList().size("event:coupons");

        if (size != null && size > 0) {
            return new EventStatusResponse("IN_PROGRESS", size);
        }
        return new EventStatusResponse("COMPLETED", 0L);
    }


}
