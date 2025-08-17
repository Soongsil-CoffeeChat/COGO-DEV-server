package com.soongsil.CoffeeChat.domain.chat.repository;

import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    List<ChatRoomUser> findByChatRoomId(Long roomId);

    Optional<ChatRoomUser> findByChatRoomIdAndUserId(Long roomId, Long userId);
}
