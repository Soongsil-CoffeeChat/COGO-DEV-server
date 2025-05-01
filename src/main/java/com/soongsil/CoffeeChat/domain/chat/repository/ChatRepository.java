package com.soongsil.CoffeeChat.domain.chat.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soongsil.CoffeeChat.domain.chat.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Page<Chat> findByChatRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    Page<Chat> findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            Long roomId, LocalDateTime before, Pageable pageable);

    Optional<Chat> findTopByChatRoomIdOrderByCreatedAtDesc(Long roomId);
}
