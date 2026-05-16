package com.soongsil.CoffeeChat.domain.assignedcoupon.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soongsil.CoffeeChat.domain.assignedcoupon.dto.*;
import com.soongsil.CoffeeChat.domain.assignedcoupon.message.AssignedCouponIssuedEvent;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignedCouponService {
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${event.store.pin}")
    private String storePin;

    private User findUserByUsername(String username) {
        return userRepository
                .findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
    }

    // 전화번호 정규화 (하이픈, 공백 제거)
    private String normalizePhoneNum(String phoneNum) {
        if (phoneNum == null) return null;
        return phoneNum.replaceAll("[^0-9]", "");
    }

    private LocalDateTime parseDateTimeOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDateTime.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    // 관리자: 지정 쿠폰 대상자 일괄 등록
    public AssignedCouponRegisterResult registerTargets(List<AssignedCouponTargetRequest> targets) {
        int newCount = 0;
        int dupCount = 0;
        List<String> failed = new ArrayList<>();

        for (AssignedCouponTargetRequest target : targets) {
            // null 요소 가드 (배치 중단 방지)
            if (target == null) {
                failed.add(null);
                continue;
            }

            String phoneNum = normalizePhoneNum(target.phoneNum());
            String name = target.name();

            // name, phoneNum blank 검증
            if (phoneNum == null
                    || phoneNum.isBlank()
                    || name == null
                    || name.isBlank()) {
                failed.add(target.phoneNum());
                continue;
            }

            String targetKey = "assigned-coupon:target:" + phoneNum;

            try {
                // hash 등록 시도 -> ticket TARGETED 로 초기화
                if (redisTemplate.hasKey(targetKey)) {
                    dupCount++;
                    continue;
                }

                redisTemplate
                        .opsForHash()
                        .putAll(
                                targetKey,
                                Map.of(
                                        "name", target.name(),
                                        "status", "TARGETED",
                                        "registeredAt", LocalDateTime.now().toString()));
                newCount++;

            } catch (Exception e) {
                // hash 등록 실패
                log.error("지정 쿠폰 대상자 등록 실패. phoneNum={}", phoneNum, e);
                failed.add(phoneNum);
            }
        }

        return new AssignedCouponRegisterResult(targets.size(), newCount, dupCount, failed);
    }

    // 유저: 보관한 진입 -> 지정 쿠폰 발급 자격 확인
    @Transactional(readOnly = true)
    public AssignedCouponCheckResponse checkEligibility(String username) {
        User user = findUserByUsername(username);

        if (user.getPhoneNum() == null || user.getName() == null) {
            return AssignedCouponCheckResponse.notEligible();
        }

        String phoneNum = normalizePhoneNum(user.getPhoneNum());
        String targetKey = "assigned-coupon:target:" + phoneNum;
        Map<Object, Object> target = redisTemplate.opsForHash().entries(targetKey);

        if (target.isEmpty()) {
            return AssignedCouponCheckResponse.notEligible();
        }

        // phoneNum -> 이름 일치 검증
        String targetName = (String) target.get("name");
        if (!user.getName().equals(targetName)) {
            log.warn(
                    "지정 쿠폰 이름 불일치 - username={}, userName={}, targetName={}",
                    username,
                    user.getName(),
                    targetName);
            return AssignedCouponCheckResponse.notEligible();
        }

        String status = (String) target.get("status");
        boolean alreadyIssued = "USED".equals(status);

        return AssignedCouponCheckResponse.builder()
                .eligible(true)
                .alreadyIssued(alreadyIssued)
                .name(targetName)
                .couponNumber((String) target.get("couponNumber"))
                .status(status)
                .issuedAt(parseDateTimeOrNull((String) target.get("issuedAt")))
                .usedAt(parseDateTimeOrNull((String) target.get("usedAt")))
                .build();
    }

    // 유저: 매장 PIN 인증 -> 지정 쿠폰 발급 및 사용 처리 (재사용 불가)
    @Transactional(readOnly = true)
    public AssignedCouponResponse issueCoupon(String username, String inputPin) {
        if (!storePin.equals(inputPin)) {
            throw new GlobalException(GlobalErrorCode.EVENT_PIN_MISMATCH);
        }

        User user = findUserByUsername(username);
        if (user.getPhoneNum() == null || user.getName() == null) {
            throw new GlobalException(GlobalErrorCode.ASSIGNED_COUPON_PHONE_NOT_SET);
        }

        String phoneNum = normalizePhoneNum(user.getPhoneNum());
        String targetKey = "assigned-coupon:target:" + phoneNum;

        // 분산락 -> fine-grained locking
        String lockKey = "lock:assigned-coupon:issue:" + phoneNum;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(3, TimeUnit.SECONDS)) {
                throw new GlobalException(GlobalErrorCode.EVENT_CONCURRENCY_ERROR);
            }

            // 락 내 대상자, 이름 일치 재검증
            Map<Object, Object> target = redisTemplate.opsForHash().entries(targetKey);
            if (target.isEmpty()) {
                throw new GlobalException(GlobalErrorCode.ASSIGNED_COUPON_NOT_TARGET);
            }
            if (!user.getName().equals(target.get("name"))) {
                throw new GlobalException(GlobalErrorCode.ASSIGNED_COUPON_NOT_TARGET);
            }

            // 이미 사용 되었는가 확인
            String currentStatus = (String) target.get("status");
            if ("USED".equals(currentStatus)) {
                throw new GlobalException(GlobalErrorCode.ASSIGNED_COUPON_ALREADY_ISSUED);
            }

            // Redis INCR -> 순차 번호 발급
            Long currentSeq = redisTemplate.opsForValue().increment("assigned-coupon:seq");
            String couponNumber = String.format("AC-%04d", currentSeq != null ? currentSeq : 0);
            LocalDateTime now = LocalDateTime.now();

            // 상태 업데이트 -> 사용 처리 및 카운터 기록
            redisTemplate
                    .opsForHash()
                    .putAll(
                            targetKey,
                            Map.of(
                                    "status",
                                    "USED",
                                    "couponNumber",
                                    couponNumber,
                                    "issuedAt",
                                    now.toString(),
                                    "usedAt",
                                    now.toString(),
                                    "claimedBy",
                                    username));
            redisTemplate.opsForValue().increment("assigned-coupon:used:count");

            // 비동기 로깅 이벤트 발행
            eventPublisher.publishEvent(
                    new AssignedCouponIssuedEvent(
                            username, user.getName(), phoneNum, couponNumber, now));

            return AssignedCouponResponse.builder()
                    .couponNumber(couponNumber)
                    .name(user.getName())
                    .status("USED")
                    .issuedAt(now)
                    .usedAt(now)
                    .build();

        } catch (InterruptedException e) {
            // 인터럽트 처리 -> 플래그 복원
            Thread.currentThread().interrupt();
            throw new IllegalStateException("동시성 처리 중 인터럽트가 발생했습니다.");
        } finally {
            // 락 소유권 확인후 해제
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
