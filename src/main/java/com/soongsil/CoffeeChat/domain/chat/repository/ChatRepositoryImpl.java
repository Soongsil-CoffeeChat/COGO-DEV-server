package com.soongsil.CoffeeChat.domain.chat.repository;

import static com.soongsil.CoffeeChat.domain.chat.entity.QChat.chat;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.domain.chat.entity.Chat;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Chat> findFirstPageChats(Long roomId, int size) {
        return queryFactory
                .selectFrom(chat)
                .where(chat.chatRoom.id.eq(roomId))
                .orderBy(chat.createdAt.desc(), chat.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Chat> findChatsByCursor(
            Long roomId, LocalDateTime cursorCreatedAt, Long cursorId, int size) {
        return queryFactory
                .selectFrom(chat)
                .where(
                        chat.chatRoom.id.eq(roomId),
                        chat.createdAt
                                .lt(cursorCreatedAt) // 1. createdAt 가 이전인 것
                                .or(
                                        chat.createdAt // 2. createdAt 가 갖고 chatId가 작은 것
                                                .eq(cursorCreatedAt)
                                                .and(chat.id.lt(cursorId))))
                .orderBy(chat.createdAt.desc(), chat.id.desc())
                .limit(size)
                .fetch();
    }
}
