package com.soongsil.CoffeeChat.domain.assignedcoupon.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import com.soongsil.CoffeeChat.domain.assignedcoupon.dto.AssignedCouponCheckResponse;
import com.soongsil.CoffeeChat.domain.assignedcoupon.dto.AssignedCouponRegisterResult;
import com.soongsil.CoffeeChat.domain.assignedcoupon.dto.AssignedCouponResponse;
import com.soongsil.CoffeeChat.domain.assignedcoupon.dto.AssignedCouponTargetRequest;
import com.soongsil.CoffeeChat.domain.assignedcoupon.message.AssignedCouponIssuedEvent;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import com.soongsil.CoffeeChat.domain.user.repository.UserRepository;
import com.soongsil.CoffeeChat.global.exception.GlobalErrorCode;
import com.soongsil.CoffeeChat.global.exception.GlobalException;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssignedCouponService 테스트")
class AssignedCouponServiceTest {

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private RedissonClient redissonClient;
    @Mock private UserRepository userRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @Mock private ValueOperations<String, String> valueOperations;
    @Mock private HashOperations<String, Object, Object> hashOperations;
    @Mock private RLock rLock;

    @InjectMocks private AssignedCouponService assignedCouponService;

    private static final String STORE_PIN = "1234";
    private static final String USERNAME = "test_user_001";
    private static final String USER_NAME = "가나다";
    private static final String PHONE_NUM_RAW = "010-1111-2222";
    private static final String PHONE_NUM_NORMALIZED = "01011112222";
    private static final String TARGET_KEY = "assigned-coupon:target:" + PHONE_NUM_NORMALIZED;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(assignedCouponService, "storePin", STORE_PIN);
    }

    private User mockUser(String name, String phoneNum) {
        User user = mock(User.class);
        lenient().when(user.getName()).thenReturn(name);
        lenient().when(user.getPhoneNum()).thenReturn(phoneNum);
        return user;
    }

    private Map<Object, Object> targetedHash() {
        Map<Object, Object> map = new HashMap<>();
        map.put("name", USER_NAME);
        map.put("status", "TARGETED");
        map.put("registeredAt", LocalDateTime.now().toString());
        return map;
    }

    private Map<Object, Object> usedHash(String couponNumber) {
        Map<Object, Object> map = new HashMap<>();
        map.put("name", USER_NAME);
        map.put("status", "USED");
        map.put("couponNumber", couponNumber);
        map.put("issuedAt", LocalDateTime.now().toString());
        map.put("usedAt", LocalDateTime.now().toString());
        map.put("claimedBy", USERNAME);
        return map;
    }

    @Nested
    @DisplayName("대상자 등록 (registerTargets)")
    class RegisterTargets {

        @Test
        @DisplayName("신규 대상자 등록 성공 - 전화번호 정규화 확인")
        void registerNewTargets_success() {
            // given
            List<AssignedCouponTargetRequest> requests =
                    List.of(
                            new AssignedCouponTargetRequest("가나다", "010-1111-2222"),
                            new AssignedCouponTargetRequest("홍길동", "01022223333"));
            given(redisTemplate.hasKey(anyString())).willReturn(false);
            given(redisTemplate.opsForHash()).willReturn(hashOperations);

            // when
            AssignedCouponRegisterResult result = assignedCouponService.registerTargets(requests);

            // then
            assertThat(result.totalRequested()).isEqualTo(2);
            assertThat(result.newlyRegistered()).isEqualTo(2);
            assertThat(result.duplicated()).isZero();
            assertThat(result.failedPhoneNums()).isEmpty();

            verify(hashOperations).putAll(eq("assigned-coupon:target:01011112222"), anyMap());
            verify(hashOperations).putAll(eq("assigned-coupon:target:01022223333"), anyMap());
        }

        @Test
        @DisplayName("중복 대상자는 duplicated 카운트로 분류")
        void registerDuplicatedTarget() {
            // given
            List<AssignedCouponTargetRequest> requests =
                    List.of(new AssignedCouponTargetRequest("가나다", "010-1111-2222"));
            given(redisTemplate.hasKey(TARGET_KEY)).willReturn(true);

            // when
            AssignedCouponRegisterResult result = assignedCouponService.registerTargets(requests);

            // then
            assertThat(result.newlyRegistered()).isZero();
            assertThat(result.duplicated()).isEqualTo(1);
            verify(hashOperations, never()).putAll(anyString(), anyMap());
        }

        @Test
        @DisplayName("name 누락 시 failed 처리")
        void registerWithMissingName() {
            // given
            List<AssignedCouponTargetRequest> requests =
                    List.of(new AssignedCouponTargetRequest(null, "010-1111-2222"));

            // when
            AssignedCouponRegisterResult result = assignedCouponService.registerTargets(requests);

            // then
            assertThat(result.newlyRegistered()).isZero();
            assertThat(result.failedPhoneNums()).containsExactly("010-1111-2222");
        }

        @Test
        @DisplayName("phoneNum 누락 시 failed 처리")
        void registerWithMissingPhoneNum() {
            // given
            List<AssignedCouponTargetRequest> requests =
                    List.of(new AssignedCouponTargetRequest("가나다", null));

            // when
            AssignedCouponRegisterResult result = assignedCouponService.registerTargets(requests);

            // then
            assertThat(result.newlyRegistered()).isZero();
            assertThat(result.failedPhoneNums()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("자격 확인 (checkEligibility)")
    class CheckEligibility {

        @Test
        @DisplayName("대상자 명단에 없으면 eligible=false")
        void notInTargetList() {
            // given
            User user = mockUser(USER_NAME, PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(new HashMap<>());

            // when
            AssignedCouponCheckResponse response = assignedCouponService.checkEligibility(USERNAME);

            // then
            assertThat(response.isEligible()).isFalse();
            assertThat(response.isAlreadyIssued()).isFalse();
        }

        @Test
        @DisplayName("phoneNum이 null 인 유저는 eligible=false")
        void userWithoutPhoneNum() {
            // given
            User user = mockUser(USER_NAME, null);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));

            // when
            AssignedCouponCheckResponse response = assignedCouponService.checkEligibility(USERNAME);

            // then
            assertThat(response.isEligible()).isFalse();
            verify(redisTemplate, never()).opsForHash();
        }

        @Test
        @DisplayName("phoneNum 일치하나 name 다르면 eligible=false")
        void nameMismatch() {
            // given
            User user = mockUser("악의적사용자", PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(targetedHash());

            // when
            AssignedCouponCheckResponse response = assignedCouponService.checkEligibility(USERNAME);

            // then
            assertThat(response.isEligible()).isFalse();
        }

        @Test
        @DisplayName("대상자이고 미발급 상태면 eligible=true, alreadyIssued=false")
        void eligibleAndNotIssued() {
            // given
            User user = mockUser(USER_NAME, PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(targetedHash());

            // when
            AssignedCouponCheckResponse response = assignedCouponService.checkEligibility(USERNAME);

            // then
            assertThat(response.isEligible()).isTrue();
            assertThat(response.isAlreadyIssued()).isFalse();
            assertThat(response.getName()).isEqualTo(USER_NAME);
            assertThat(response.getStatus()).isEqualTo("TARGETED");
            assertThat(response.getCouponNumber()).isNull();
        }

        @Test
        @DisplayName("이미 발급된 대상자는 eligible=true, alreadyIssued=true (쿠폰 번호 반환)")
        void alreadyIssuedTarget() {
            // given
            User user = mockUser(USER_NAME, PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(usedHash("AC-0001"));

            // when
            AssignedCouponCheckResponse response = assignedCouponService.checkEligibility(USERNAME);

            // then
            assertThat(response.isEligible()).isTrue();
            assertThat(response.isAlreadyIssued()).isTrue();
            assertThat(response.getCouponNumber()).isEqualTo("AC-0001");
            assertThat(response.getStatus()).isEqualTo("USED");
        }
    }

    @Nested
    @DisplayName("쿠폰 발급 (issueCoupon)")
    class IssueCoupon {

        @Test
        @DisplayName("PIN 불일치 시 EVENT_PIN_MISMATCH 예외")
        void wrongPin() {
            // when, then
            assertThatThrownBy(() -> assignedCouponService.issueCoupon(USERNAME, "WRONG_PIN"))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue(
                            "globalErrorCode", GlobalErrorCode.EVENT_PIN_MISMATCH);

            verifyNoInteractions(userRepository, redisTemplate, redissonClient, eventPublisher);
        }

        @Test
        @DisplayName("phoneNum이 null 이면 ASSIGNED_COUPON_PHONE_NOT_SET 예외")
        void noPhoneNum() {
            // given
            User user = mockUser(USER_NAME, null);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));

            // when, then
            assertThatThrownBy(() -> assignedCouponService.issueCoupon(USERNAME, STORE_PIN))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue(
                            "globalErrorCode", GlobalErrorCode.ASSIGNED_COUPON_PHONE_NOT_SET);

            verifyNoInteractions(redissonClient, eventPublisher);
        }

        @Test
        @DisplayName("락 획득 실패 시 EVENT_CONCURRENCY_ERROR 예외")
        void lockTimeout() throws InterruptedException {
            // given
            User user = mockUser(USER_NAME, PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), any(TimeUnit.class))).willReturn(false);

            // when, then
            assertThatThrownBy(() -> assignedCouponService.issueCoupon(USERNAME, STORE_PIN))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue(
                            "globalErrorCode", GlobalErrorCode.EVENT_CONCURRENCY_ERROR);

            verify(rLock, never()).unlock();
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("대상자 명단에 없으면 ASSIGNED_COUPON_NOT_TARGET 예외")
        void notInTargetList() throws InterruptedException {
            // given
            User user = mockUser(USER_NAME, PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isLocked()).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(new HashMap<>());

            // when, then
            assertThatThrownBy(() -> assignedCouponService.issueCoupon(USERNAME, STORE_PIN))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue(
                            "globalErrorCode", GlobalErrorCode.ASSIGNED_COUPON_NOT_TARGET);

            verify(rLock).unlock();
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("이름 불일치 시 ASSIGNED_COUPON_NOT_TARGET 예외 (락 안에서 재검증)")
        void nameMismatchInsideLock() throws InterruptedException {
            // given
            User user = mockUser("테스트악의적사용자", PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isLocked()).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(targetedHash());

            // when, then
            assertThatThrownBy(() -> assignedCouponService.issueCoupon(USERNAME, STORE_PIN))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue(
                            "globalErrorCode", GlobalErrorCode.ASSIGNED_COUPON_NOT_TARGET);

            verify(rLock).unlock();
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("이미 발급된 쿠폰은 ASSIGNED_COUPON_ALREADY_ISSUED 예외 (재발급 차단)")
        void alreadyIssued() throws InterruptedException {
            // given
            User user = mockUser(USER_NAME, PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isLocked()).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(usedHash("AC-0001"));

            // when, then
            assertThatThrownBy(() -> assignedCouponService.issueCoupon(USERNAME, STORE_PIN))
                    .isInstanceOf(GlobalException.class)
                    .hasFieldOrPropertyWithValue(
                            "globalErrorCode", GlobalErrorCode.ASSIGNED_COUPON_ALREADY_ISSUED);

            verify(rLock).unlock();
            verifyNoInteractions(eventPublisher);
            verify(valueOperations, never()).increment(anyString());
        }

        @Test
        @DisplayName("정상 발급 - 쿠폰 번호 AC-포맷, Redis 갱신, S3 이벤트 발행, 락 해제")
        void issueSuccess() throws InterruptedException {
            // given
            User user = mockUser(USER_NAME, PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isLocked()).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(targetedHash());
            given(valueOperations.increment("assigned-coupon:seq")).willReturn(1L);
            given(valueOperations.increment("assigned-coupon:used:count")).willReturn(1L);

            // when
            AssignedCouponResponse response =
                    assignedCouponService.issueCoupon(USERNAME, STORE_PIN);

            // then - 응답 검증
            assertThat(response.getCouponNumber()).isEqualTo("AC-0001");
            assertThat(response.getName()).isEqualTo(USER_NAME);
            assertThat(response.getStatus()).isEqualTo("USED");
            assertThat(response.getIssuedAt()).isNotNull();
            assertThat(response.getUsedAt()).isNotNull();

            // then - Redis 상태 갱신 검증
            ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
            verify(hashOperations).putAll(eq(TARGET_KEY), mapCaptor.capture());
            Map<String, String> updated = mapCaptor.getValue();
            assertThat(updated.get("status")).isEqualTo("USED");
            assertThat(updated.get("couponNumber")).isEqualTo("AC-0001");
            assertThat(updated.get("claimedBy")).isEqualTo(USERNAME);

            // then - S3 로깅 이벤트 발행 검증
            ArgumentCaptor<AssignedCouponIssuedEvent> eventCaptor =
                    ArgumentCaptor.forClass(AssignedCouponIssuedEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            AssignedCouponIssuedEvent event = eventCaptor.getValue();
            assertThat(event.username()).isEqualTo(USERNAME);
            assertThat(event.name()).isEqualTo(USER_NAME);
            assertThat(event.phoneNum()).isEqualTo(PHONE_NUM_NORMALIZED);
            assertThat(event.couponNumber()).isEqualTo("AC-0001");
            assertThat(event.issuedAt()).isNotNull();

            // then - 락 해제 확인
            verify(rLock).unlock();
        }

        @Test
        @DisplayName("발급 성공 시 사용 카운트 +1 증가")
        void incrementUsedCount() throws InterruptedException {
            // given
            User user = mockUser(USER_NAME, PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isLocked()).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(targetedHash());
            given(valueOperations.increment("assigned-coupon:seq")).willReturn(3L);

            // when
            assignedCouponService.issueCoupon(USERNAME, STORE_PIN);

            // then
            verify(valueOperations).increment("assigned-coupon:used:count");
        }

        @Test
        @DisplayName("쿠폰 번호 포맷 - AC-XXXX (4자리 0 패딩)")
        void couponNumberFormatting() throws InterruptedException {
            // given
            User user = mockUser(USER_NAME, PHONE_NUM_RAW);
            given(userRepository.findByUsernameAndIsDeletedFalse(USERNAME))
                    .willReturn(Optional.of(user));
            given(redissonClient.getLock(anyString())).willReturn(rLock);
            given(rLock.tryLock(anyLong(), any(TimeUnit.class))).willReturn(true);
            given(rLock.isLocked()).willReturn(true);
            given(rLock.isHeldByCurrentThread()).willReturn(true);
            given(redisTemplate.opsForHash()).willReturn(hashOperations);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(hashOperations.entries(TARGET_KEY)).willReturn(targetedHash());
            given(valueOperations.increment("assigned-coupon:seq")).willReturn(42L);

            // when
            AssignedCouponResponse response =
                    assignedCouponService.issueCoupon(USERNAME, STORE_PIN);

            // then
            assertThat(response.getCouponNumber()).isEqualTo("AC-0042");
        }
    }

    @Test
    @DisplayName("null 요소가 섞여있어도 배치가 중단되지 않고 failed로 처리")
    void registerWithNullElement() {
        // given
        List<AssignedCouponTargetRequest> requests = new ArrayList<>();
        requests.add(null);
        requests.add(new AssignedCouponTargetRequest("가나다", "010-1111-2222"));
        given(redisTemplate.hasKey(anyString())).willReturn(false);
        given(redisTemplate.opsForHash()).willReturn(hashOperations);

        // when
        AssignedCouponRegisterResult result =
                assignedCouponService.registerTargets(requests);

        // then
        assertThat(result.newlyRegistered()).isEqualTo(1);
        assertThat(result.failedPhoneNums()).contains((String) null);
    }

    @Test
    @DisplayName("공백 이름은 failed 처리 (좀비 데이터 방지)")
    void registerWithBlankName() {
        // given
        List<AssignedCouponTargetRequest> requests =
                List.of(new AssignedCouponTargetRequest("   ", "010-1111-2222"));

        // when
        AssignedCouponRegisterResult result =
                assignedCouponService.registerTargets(requests);

        // then
        assertThat(result.newlyRegistered()).isZero();
        assertThat(result.failedPhoneNums()).containsExactly("010-1111-2222");
    }
}
