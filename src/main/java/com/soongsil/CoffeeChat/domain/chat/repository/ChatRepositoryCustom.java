package com.soongsil.CoffeeChat.domain.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.soongsil.CoffeeChat.domain.chat.entity.Chat;

public interface ChatRepositoryCustom {

    // 최초 페이지 조회 (커서 X)
    List<Chat> findFirstPageChats(Long roomId, int size);

    // 다음 페이지 조회 (커서 기반)
    List<Chat> findChatsByCursor(
            Long roomId, LocalDateTime cursorCreatedAt, Long cursorId, int size);
}
