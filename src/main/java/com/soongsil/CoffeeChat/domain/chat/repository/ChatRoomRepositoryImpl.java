package com.soongsil.CoffeeChat.domain.chat.repository;

import static com.soongsil.CoffeeChat.domain.chat.entity.QChatRoom.chatRoom;
import static com.soongsil.CoffeeChat.domain.chat.entity.QChatRoomUser.chatRoomUser;
import static com.soongsil.CoffeeChat.domain.report.entity.QReport.report;

import java.util.List;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.domain.chat.entity.ChatRoom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 활성 채팅방 조건 - currentUser 참여 중 방 + currentUser가 신고한 사람 존재하지 않음
    private Predicate[] activeChatRoomConditions(Long currentUserId) {
        return new Predicate[]{
                chatRoom.id.in(
                        JPAExpressions.select(chatRoomUser.chatRoom.id)
                                .from(chatRoomUser)
                                .where(chatRoomUser.user.id.eq(currentUserId))
                ),
                JPAExpressions.selectOne()
                        .from(chatRoomUser)
                        .where(
                                chatRoomUser.chatRoom.id.eq(chatRoom.id),
                                chatRoomUser.user.id.ne(currentUserId),
                                chatRoomUser.user.id.in(
                                        JPAExpressions.select(report.reportedUserId)
                                                .from(report)
                                                .where(report.reporterId.eq(currentUserId))))
                        .notExists()
        };
    }

    @Override
    public Page<ChatRoom> findActiveChatRoomsByUserId(Long currentUserId, Pageable pageable) {

        Predicate[] conditions = activeChatRoomConditions(currentUserId);

        // 조건 충족 채팅방 리스트 조회
        List<ChatRoom> content =
                queryFactory
                        .selectFrom(chatRoom)
                        .where(conditions)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(chatRoom.updatedDate.desc())
                        .fetch();

        // 총 건수 조회
        Long total =
                queryFactory
                        .select(chatRoom.count())
                        .from(chatRoom)
                        .where(conditions)
                        .fetchOne();
        long totalCount = total != null ? total : 0L;
        return new PageImpl<>(content, pageable, totalCount);
    }

}
