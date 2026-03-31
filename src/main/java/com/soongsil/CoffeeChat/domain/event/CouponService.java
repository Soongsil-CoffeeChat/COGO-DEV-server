package com.soongsil.CoffeeChat.domain.event;

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
import com.soongsil.CoffeeChat.domain.event.dto.CouponIssuedEvent;
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

    // 멘토: QR 토큰 발급 로직
    @Transactional(readOnly = true)
    public String generateQrToken(Long applicationId, String username) {
        User user = findUserByUsername(username);
        if (!user.isMentor()) {
            throw new IllegalStateException("멘토만 QR 코드를 발급할 수 있습니다.");
        }

        Application application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(
                                () -> new GlobalException(GlobalErrorCode.APPLICATION_NOT_FOUND));

        if (!application.getMentor().getId().equals(user.getMentor().getId())) {
            throw new IllegalStateException("본인의 커피챗에 대해서만 QR을 발급할 수 있습니다.");
        }

        Boolean isAlreadyIssued =
                redisTemplate
                        .opsForSet()
                        .isMember("event:issued:applications", applicationId.toString());
        if (Boolean.TRUE.equals(isAlreadyIssued)) {
            throw new IllegalStateException("이미 쿠폰이 발급된 커피챗입니다.");
        }

        if (application.getPossibleDate() == null) {
            throw new IllegalStateException("커피챗 일정이 설정 되어 있지 않습니다.");
        }

        LocalDateTime applicationEndTime =
                LocalDateTime.of(
                        application.getPossibleDate().getDate(),
                        application.getPossibleDate().getEndTime());

        LocalDateTime expireTime = applicationEndTime.plusWeeks(1);

        long ttlSeconds = Duration.between(LocalDateTime.now(), expireTime).getSeconds();

        if (ttlSeconds <= 0) {
            throw new IllegalStateException("만료 기간(1주일)이 지난 커피챗 입니다.");
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
            throw new IllegalArgumentException("매장 핀 번호가 일치하지 않습니다.");
        }

        // 1. 사용자 역할, 토큰 유효성 검증
        User user = findUserByUsername(username);

        if (!user.isMentee()) {
            throw new IllegalStateException("멘티만 스캔하여 쿠폰을 발급받을 수 있습니다.");
        }

        String tokenKey = "event:qr:token:" + qrToken;
        String applicationIdString = redisTemplate.opsForValue().get(tokenKey);

        if (applicationIdString == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 QR 코드입니다.");
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
                throw new IllegalStateException("현재 처리 중인 요청입니다. 잠시 후 다시 시도해주세요.");
            }

            // 1) 채팅방 1회 발급 제한 검증
            Boolean isAlreadyIssued =
                    redisTemplate
                            .opsForSet()
                            .isMember("event:issued:applications", applicationIdString);
            if (Boolean.TRUE.equals(isAlreadyIssued)) {
                throw new IllegalStateException("이미 쿠폰이 발급된 커피챗입니다.");
            }

            // 2) 멘티 이벤트 참여 횟수 제한 검증
            checkMenteeLimit(menteeId);

            // 3) 쿠폰 발급
            String couponUrl = redisTemplate.opsForList().leftPop("event:coupons");
            if (couponUrl == null) {
                throw new IllegalStateException("준비된 쿠폰이 모두 소진되었습니다.");
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

    private void checkMenteeLimit(Long menteeId) {
        String userKey = "event:user:participation:" + menteeId;
        String countString = redisTemplate.opsForValue().get(userKey);

        if (countString != null && Integer.parseInt(countString) >= 2) {
            throw new IllegalStateException("이벤트 참여 횟수 (최대2회)를 초과했습니다.");
        }
    }
}
