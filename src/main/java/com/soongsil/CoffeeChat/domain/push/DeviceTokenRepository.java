package com.soongsil.CoffeeChat.domain.push;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// @Repository 유무 차이?
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findAllByUserId(Long userId);

    Optional<DeviceToken> findByUserIdAndToken(Long userId, String token);

    void deleteByUserIdAndToken(Long userId, String token);
}
