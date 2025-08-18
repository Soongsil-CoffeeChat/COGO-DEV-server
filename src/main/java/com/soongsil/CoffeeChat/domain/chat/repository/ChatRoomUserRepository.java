package com.soongsil.CoffeeChat.domain.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoomUser;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    List<ChatRoomUser> findByChatRoomId(Long roomId);

    Optional<ChatRoomUser> findByChatRoomIdAndUserId(Long roomId, Long userId);
}
