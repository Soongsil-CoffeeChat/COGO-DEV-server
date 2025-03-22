package com.soongsil.CoffeeChat.repository.User;

import static com.soongsil.CoffeeChat.entity.QMentee.*;
import static com.soongsil.CoffeeChat.entity.QMentor.*;
import static com.soongsil.CoffeeChat.entity.QPossibleDate.*;
import static com.soongsil.CoffeeChat.entity.QUser.*;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.dto.UserRequest.*;
import com.soongsil.CoffeeChat.entity.QMentee;
import com.soongsil.CoffeeChat.entity.QMentor;
import com.soongsil.CoffeeChat.entity.QUser;
import com.soongsil.CoffeeChat.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public User findByMentorIdWithFetch(Long mentorId) {
        return queryFactory
                .selectFrom(user)
                .join(user.mentor, mentor)
                .fetchJoin()
                .join(mentor.possibleDates, possibleDate)
                .fetchJoin()
                .where(mentor.id.eq(mentorId))
                .fetchOne();
    }

    @Override
    public User findByUsernameWithFetch(String username) {
        return queryFactory
                .selectFrom(user)
                .join(user.mentee, mentee)
                .fetchJoin()
                .where(user.username.eq(username))
                .fetchOne();
    }

    @Override
    public UserGetRequest findUserInfoByUsername(String username) {
        QUser user = QUser.user;
        QMentor mentor = QMentor.mentor;
        QMentee mentee = QMentee.mentee;

        // 역할을 결정하는 표현식
        StringExpression roleExpression =
                new CaseBuilder()
                        .when(mentor.isNotNull())
                        .then("MENTOR")
                        .when(mentee.isNotNull())
                        .then("MENTEE")
                        .otherwise("UNKNOWN");

        return queryFactory
                .select(
                        Projections.constructor(
                                UserGetRequest.class,
                                user.name,
                                user.email,
                                user.phoneNum,
                                roleExpression,
                                mentor.part.coalesce(mentee.part), // 멘토의 part가 없으면 멘티의 part 사용
                                mentor.club, // 멘토의 club, 멘티인 경우 null
                                user.picture))
                .from(user)
                .leftJoin(user.mentor, mentor)
                .leftJoin(user.mentee, mentee)
                .where(user.username.eq(username))
                .fetchOne();
    }
}
