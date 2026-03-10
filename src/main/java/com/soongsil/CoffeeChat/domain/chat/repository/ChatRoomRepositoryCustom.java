package com.soongsil.CoffeeChat.domain.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoom;

public interface ChatRoomRepositoryCustom {
    Page<ChatRoom> findActiveChatRoomsByUserId(Long currentUserId, Pageable pageable);
}
