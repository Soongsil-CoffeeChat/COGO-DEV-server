package com.soongsil.CoffeeChat.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.soongsil.CoffeeChat.domain.mentee.entity.QMentee;
import com.soongsil.CoffeeChat.domain.mentor.entity.QMentor;
import com.soongsil.CoffeeChat.domain.user.dto.UserRequest.UserGetRequest;
import com.soongsil.CoffeeChat.domain.user.entity.QUser;
import com.soongsil.CoffeeChat.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import static com.soongsil.CoffeeChat.domain.mentee.entity.QMentee.mentee;
import static com.soongsil.CoffeeChat.domain.mentor.entity.QMentor.mentor;
import static com.soongsil.CoffeeChat.domain.possibleDate.entity.QPossibleDate.possibleDate;
import static com.soongsil.CoffeeChat.domain.user.entity.QUser.user;

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

        return queryFactory
                .select(
                        Projections.constructor(
                                UserGetRequest.class,
                                user.name,
                                user.email,
                                user.phoneNum,
                                user.role,
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
