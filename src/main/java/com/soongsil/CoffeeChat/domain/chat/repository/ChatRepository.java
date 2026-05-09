package com.soongsil.CoffeeChat.domain.chat.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soongsil.CoffeeChat.domain.chat.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long>, ChatRepositoryCustom {

    Page<Chat> findByChatRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    Page<Chat> findByChatRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            Long roomId, LocalDateTime before, Pageable pageable);

    Optional<Chat> findTopByChatRoomIdOrderByCreatedAtDesc(Long roomId);

    Optional<Chat> findTopByChatRoomIdOrderByCreatedAtDescIdDesc(Long roomId);

    @Query(
            """
            SELECT COUNT(c)
            FROM Chat c
            WHERE c.chatRoom.id = :roomId
              AND c.sender.id <> :userId
              AND (:lastReadChatId IS NULL OR c.id > :lastReadChatId)
            """)
    Long countUnreadMessages(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId,
            @Param("lastReadChatId") Long lastReadChatId);
}
