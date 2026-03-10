package com.soongsil.CoffeeChat.domain.chat.repository;

import static com.soongsil.CoffeeChat.domain.chat.entity.QChatRoom.chatRoom;
import static com.soongsil.CoffeeChat.domain.chat.entity.QChatRoomUser.chatRoomUser;
import static com.soongsil.CoffeeChat.domain.report.entity.QReport.report;

import java.util.List;

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

    @Override
    public Page<ChatRoom> findActiveChatRoomsByUserId(Long currentUserId, Pageable pageable) {

        // 조건에 맞는 채팅방 리스트 조회
        List<ChatRoom> content =
                queryFactory
                        .selectFrom(chatRoom)
                        .where(
                                // 조건 1. currentUser가 참여 중인 방
                                chatRoom.id.in(
                                        JPAExpressions.select(chatRoomUser.chatRoom.id)
                                                .from(chatRoomUser)
                                                .where(chatRoomUser.user.id.eq(currentUserId))),
                                JPAExpressions.selectOne()
                                        .from(chatRoomUser)
                                        .where(
                                                chatRoomUser.chatRoom.id.eq(chatRoom.id),
                                                chatRoomUser.user.id.ne(currentUserId),
                                                chatRoomUser.user.id.in(
                                                        JPAExpressions.select(report.reportedUserId)
                                                                .from(report)
                                                                .where(
                                                                        report.reporterId.eq(
                                                                                currentUserId))))
                                        .notExists())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(chatRoom.updatedDate.desc())
                        .fetch();

        // 조건 2. 방 참여자 중 currentUser가 신고한 사람 없어야 함
        Long total =
                queryFactory
                        .select(chatRoom.count())
                        .from(chatRoom)
                        .where(
                                chatRoom.id.in(
                                        JPAExpressions.select(chatRoomUser.chatRoom.id)
                                                .from(chatRoomUser)
                                                .where(chatRoomUser.user.id.eq(currentUserId))),
                                JPAExpressions.selectOne()
                                        .from(chatRoomUser)
                                        .where(
                                                chatRoomUser.chatRoom.id.eq(chatRoom.id),
                                                chatRoomUser.user.id.ne(currentUserId),
                                                chatRoomUser.user.id.in(
                                                        JPAExpressions.select(report.reportedUserId)
                                                                .from(report)
                                                                .where(
                                                                        report.reporterId.eq(
                                                                                currentUserId))))
                                        .notExists())
                        .fetchOne();

        long totalCount = total != null ? total : 0L;
        return new PageImpl<>(content, pageable, totalCount);
    }
}
