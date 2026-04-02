package com.soongsil.CoffeeChat.domain.event.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.application.entity.Application;
import com.soongsil.CoffeeChat.domain.application.repository.ApplicationRepository;
import com.soongsil.CoffeeChat.domain.event.message.CouponIssuedEvent;
import com.soongsil.CoffeeChat.domain.event.dto.EventCheckResponse;
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

    private boolean isMenteeLimitExceeded(Long menteeId) {
        String userKey = "event:user:participation:" + menteeId;
        String countString = redisTemplate.opsForValue().get(userKey);
        int currentCount = (countString != null) ? Integer.parseInt(countString) : 0;

        return currentCount >= 2;
    }

    private boolean isAlreadyIssued(Long applicationId) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember("event:issued:applications", applicationId.toString())
        );
    }

    // 이벤트 참여 이력 조회
    public EventCheckResponse checkEligibility(String username, Long applicationId) {
        User user = findUserByUsername(username);

        // 해당 코고 이벤트 참여 이력 확인
        boolean alreadyIssued = isAlreadyIssued(applicationId);
        boolean limitExceeded = false;

        // 멘티 일 시 참여 이력 상한 확인 (2회)
        if (user.isMentee()) {
            Long menteeId = user.getMentee().getId();
            limitExceeded = isMenteeLimitExceeded(menteeId);
        }

        return EventCheckResponse.builder()
                .isAlreadyIssued(alreadyIssued)
                .isLimitExceeded(limitExceeded)
                .canIssue(!alreadyIssued && !limitExceeded)
                .build();
    }

    // 멘토: QR 토큰 발급 로직
    @Transactional(readOnly = true)
    public String generateQrToken(String username, Long applicationId) {
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

        if (isAlreadyIssued(applicationId)) {
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

    // 멘티: qr 검증 (1차 인증)
    @Transactional(readOnly = true)
    public void verifyQrToken(String username, String qrToken) {
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
    }

    // 멘티: qr 인증 -> 매장 핀 번호 인증 (2차 인증)
    @Transactional(readOnly = true)
    public String verifyPinAndIssueCoupon(String username, String qrToken, String inputPin) {
        if (!storePin.equals(inputPin)) {
            throw new GlobalException(GlobalErrorCode.EVENT_PIN_MISMATCH);
        }

        User user = findUserByUsername(username);
        String tokenKey = "event:qr:token:" + qrToken;
        String applicationIdString = redisTemplate.opsForValue().get(tokenKey);

        if (applicationIdString == null) {
            throw new GlobalException(GlobalErrorCode.EVENT_QR_EXPIRED);
        }

        Long applicationId = Long.parseLong(applicationIdString);
        Application application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND));

        Long menteeId = user.getMentee().getId();
        Long mentorId = application.getMentor().getId();

        String lockKey = "lock:coupon:issue:" + applicationIdString;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(3, 3, TimeUnit.SECONDS)) {
                throw new GlobalException(GlobalErrorCode.EVENT_CONCURRENCY_ERROR);
            }

            if (isAlreadyIssued(applicationId)) {
                throw new GlobalException(GlobalErrorCode.EVENT_ALREADY_ISSUED);
            }
            if (isMenteeLimitExceeded(menteeId)) {
                throw new GlobalException(GlobalErrorCode.EVENT_LIMIT_EXCEEDED);
            }

            // 쿠폰 한도 및 번호 발급
            String maxCouponsStr = redisTemplate.opsForValue().get("event:coupon:max");
            long maxCoupons = maxCouponsStr != null ? Long.parseLong(maxCouponsStr) : 0L;

            // Redis INCR -> 순차 번호 발급
            Long currentSeq = redisTemplate.opsForValue().increment("event:coupon:seq");

            if (currentSeq == null || currentSeq > maxCoupons) {
                // 초과 시 롤백 처리
                redisTemplate.opsForValue().decrement("event:coupon:seq");
                throw new GlobalException(GlobalErrorCode.EVENT_COUPON_EXHAUSTED);
            }

            String couponNumber = String.valueOf(currentSeq);

            // 상태 업데이트
            redisTemplate.opsForSet().add("event:issued:applications", applicationIdString);
            redisTemplate.opsForValue().increment("event:user:participation:" + menteeId);
            redisTemplate.delete(tokenKey);

            eventPublisher.publishEvent(
                    new CouponIssuedEvent(
                            applicationId, menteeId, mentorId, couponNumber, LocalDateTime.now()));

            return couponNumber;

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
        String maxCouponsStr = redisTemplate.opsForValue().get("event:coupon:max");
        String currentSeqStr = redisTemplate.opsForValue().get("event:coupon:seq");

        long maxCoupons = maxCouponsStr != null ? Long.parseLong(maxCouponsStr) : 0L;
        long currentSeq = currentSeqStr != null ? Long.parseLong(currentSeqStr) : 0L;

        long remaining = maxCoupons - currentSeq;

        if (remaining > 0) {
            return new EventStatusResponse("IN_PROGRESS", remaining);
        }
        return new EventStatusResponse("COMPLETED", 0L);
    }
}
